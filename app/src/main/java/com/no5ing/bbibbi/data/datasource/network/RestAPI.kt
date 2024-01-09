package com.no5ing.bbibbi.data.datasource.network

import com.no5ing.bbibbi.data.datasource.network.request.member.AddFcmTokenRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.ChangeNameRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.ChangeProfileImageRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.ImageUploadRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.JoinFamilyRequest
import com.no5ing.bbibbi.data.datasource.network.request.post.CreatePostReactionRequest
import com.no5ing.bbibbi.data.datasource.network.request.post.CreatePostRequest
import com.no5ing.bbibbi.data.datasource.network.request.post.DeletePostReactionRequest
import com.no5ing.bbibbi.data.datasource.network.response.ArrayResponse
import com.no5ing.bbibbi.data.datasource.network.response.DefaultResponse
import com.no5ing.bbibbi.data.datasource.network.response.Pagination
import com.no5ing.bbibbi.data.model.auth.AuthResult
import com.no5ing.bbibbi.data.model.auth.RefreshAuthRequest
import com.no5ing.bbibbi.data.model.auth.RegisterRequest
import com.no5ing.bbibbi.data.model.auth.SocialLoginRequest
import com.no5ing.bbibbi.data.model.family.Family
import com.no5ing.bbibbi.data.model.family.FamilySummary
import com.no5ing.bbibbi.data.model.link.DeepLink
import com.no5ing.bbibbi.data.model.member.ImageUploadLink
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.post.CalendarElement
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.model.post.PostReaction
import com.no5ing.bbibbi.data.model.post.PostReactionSummary
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface RestAPI {
    /**
     * 가족 API
     */
    interface FamilyApi {
        @POST("v1/families")
        suspend fun createFamily(): ApiResponse<Family>

        @GET("v1/families/{familyId}/summary")
        suspend fun getFamilySummary(
            @Path("familyId") familyId: String,
        ): ApiResponse<FamilySummary>
    }

    /**
     * 회원 API
     */
    interface MemberApi {
        @GET("v1/members?type=FAMILY")
        suspend fun getMembers(
            @Query("page") page: Int?,
            @Query("size") size: Int?,
        ): ApiResponse<Pagination<Member>>

        @GET("v1/members/{memberId}")
        suspend fun getMember(
            @Path("memberId") memberId: String
        ): ApiResponse<Member>

        @DELETE("v1/members/{memberId}")
        suspend fun quitMember(
            @Path("memberId") memberId: String
        ): ApiResponse<Member>

        @POST("v1/members/image-upload-request")
        suspend fun getUploadImageRequest(
            @Body body: ImageUploadRequest,
        ): ApiResponse<ImageUploadLink>

        @GET("v1/me/member-info")
        suspend fun getMeInfo(): ApiResponse<Member>

        @PUT("v1/members/name/{memberId}")
        suspend fun changeMemberName(
            @Path("memberId") memberId: String,
            @Body body: ChangeNameRequest,
        ): ApiResponse<Member>

        @PUT("v1/members/profile-image-url/{memberId}")
        suspend fun changeProfileImage(
            @Path("memberId") memberId: String,
            @Body body: ChangeProfileImageRequest,
        ): ApiResponse<Member>


        @POST("v1/me/fcm")
        suspend fun registerFcmToken(
            @Body body: AddFcmTokenRequest,
        ): ApiResponse<DefaultResponse>

        @DELETE("v1/me/fcm/{fcmToken}")
        suspend fun deleteFcmToken(
            @Path("fcmToken") fcmToken: String,
        ): ApiResponse<DefaultResponse>

        @POST("v1/me/create-family")
        suspend fun createAndJoinFamily(): ApiResponse<Family>

        @POST("v1/me/join-family")
        suspend fun joinFamilyWithToken(
            @Body body: JoinFamilyRequest,
        ): ApiResponse<Family>
    }

    /**
     * 게시물 API
     */
    interface PostApi {
        @GET("v1/posts")
        suspend fun getPosts(
            @Query("page") page: Int?,
            @Query("size") size: Int?,
            @Query("date") date: String?,
            @Query("memberId") memberId: String?,
        ): ApiResponse<Pagination<Post>>

        @GET("v1/posts/{postId}")
        suspend fun getPost(
            @Path("postId") postId: String,
        ): ApiResponse<Post>

        @POST("v1/posts")
        suspend fun createPost(
            @Body body: CreatePostRequest,
        ): ApiResponse<Post>

        @POST("v1/posts/image-upload-request")
        suspend fun getUploadPostImageRequest(
            @Body body: ImageUploadRequest,
        ): ApiResponse<ImageUploadLink>

        @GET("v1/posts/{postId}/reactions/summary")
        suspend fun getPostReactionSummary(
            @Path("postId") postId: String,
        ): ApiResponse<PostReactionSummary>

        @GET("v1/posts/{postId}/reactions")
        suspend fun getPostReactions(
            @Path("postId") postId: String,
        ): ApiResponse<ArrayResponse<PostReaction>>

        @HTTP(method = "DELETE", path = "v1/posts/{postId}/reactions", hasBody = true)
        suspend fun deletePostReactions(
            @Path("postId") postId: String,
            @Body body: DeletePostReactionRequest,
        ): ApiResponse<DefaultResponse>

        @POST("v1/posts/{postId}/reactions")
        suspend fun createPostReactions(
            @Path("postId") postId: String,
            @Body body: CreatePostReactionRequest,
        ): ApiResponse<DefaultResponse>

        @GET("v1/calendar?type=MONTHLY")
        suspend fun getMonthlyCalendar(
            @Query("yearMonth") yearMonth: String,
        ): ApiResponse<ArrayResponse<CalendarElement>>

        @GET("v1/calendar?type=WEEKLY")
        suspend fun getWeeklyCalendar(
            @Query("yearMonth") yearMonth: String,
            @Query("week") week: Int,
        ): ApiResponse<ArrayResponse<CalendarElement>>
    }

    /**
     * 인증 API
     */
    interface AuthApi {
        @POST("v1/auth/register")
        suspend fun register(
            @Body body: RegisterRequest,
        ): ApiResponse<AuthResult>

        @POST("v1/auth/social/kakao")
        suspend fun kakaoLogin(
            @Body body: SocialLoginRequest,
        ): ApiResponse<AuthResult>

        @POST("v1/auth/social/google")
        suspend fun googleLogin(
            @Body body: SocialLoginRequest,
        ): ApiResponse<AuthResult>

        @POST("v1/auth/force-token")
        suspend fun temporaryLogin(
            @Query("memberId") memberId: String,
        ): ApiResponse<AuthResult>

        @POST("v1/auth/refresh")
        suspend fun refreshToken(
            @Body body: RefreshAuthRequest,
        ): ApiResponse<AuthResult>
    }

    interface LinkApi {
        @GET("v1/links/{linkId}")
        suspend fun getLink(
            @Path("linkId") linkId: String,
        ): ApiResponse<DeepLink>

        @POST("v1/links/family/{familyId}")
        suspend fun createFamilyLink(
            @Path("familyId") familyId: String,
        ): ApiResponse<DeepLink>
    }

    /**
     * API 모음
     */
    fun getMemberApi(): MemberApi
    fun getFamilyApi(): FamilyApi
    fun getPostApi(): PostApi
    fun getAuthApi(): AuthApi
    fun getLinkApi(): LinkApi
}