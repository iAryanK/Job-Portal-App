package com.aryan.jobportalapp.domain

import com.aryan.jobportalapp.data.remote.dto.AppUserResponse
import com.aryan.jobportalapp.data.remote.dto.AppUserRole
import com.aryan.jobportalapp.data.repository.AuthRepository
import com.aryan.jobportalapp.data.repository.EmployerRepository
import com.aryan.jobportalapp.data.repository.JobSeekerRepository
import com.aryan.jobportalapp.data.repository.ProfileNotFoundException

enum class PostAuthDestination {
    RoleSelection,
    EmployerOnboarding,
    JobSeekerOnboarding,
    Home
}

class PostAuthRouter(
    private val authRepository: AuthRepository = AuthRepository(),
    private val employerRepository: EmployerRepository = EmployerRepository(),
    private val jobSeekerRepository: JobSeekerRepository = JobSeekerRepository()
) {

    suspend fun resolveFromCurrentUser(): Result<PostAuthDestination> {
        return authRepository.getCurrentUser().fold(
            onSuccess = { user -> resolveForUser(user) },
            onFailure = { Result.failure(it) }
        )
    }

    suspend fun resolveForUser(user: AppUserResponse): Result<PostAuthDestination> {
        val userId = user.id
            ?: return Result.failure(IllegalStateException("User id missing from /me response"))

        return when (user.role) {
            AppUserRole.USER, null -> Result.success(PostAuthDestination.RoleSelection)
            AppUserRole.EMPLOYER -> resolveEmployerDestination(userId)
            AppUserRole.JOBSEEKER -> resolveJobSeekerDestination(userId)
            AppUserRole.ADMIN -> Result.success(PostAuthDestination.Home)
        }
    }

    private suspend fun resolveEmployerDestination(userId: String): Result<PostAuthDestination> {
        return employerRepository.getEmployerById(userId).fold(
            onSuccess = { Result.success(PostAuthDestination.Home) },
            onFailure = { error ->
                if (error is ProfileNotFoundException) {
                    Result.success(PostAuthDestination.EmployerOnboarding)
                } else {
                    Result.failure(error)
                }
            }
        )
    }

    private suspend fun resolveJobSeekerDestination(userId: String): Result<PostAuthDestination> {
        return jobSeekerRepository.getJobSeekerById(userId).fold(
            onSuccess = { Result.success(PostAuthDestination.Home) },
            onFailure = { error ->
                if (error is ProfileNotFoundException) {
                    Result.success(PostAuthDestination.JobSeekerOnboarding)
                } else {
                    Result.failure(error)
                }
            }
        )
    }
}
