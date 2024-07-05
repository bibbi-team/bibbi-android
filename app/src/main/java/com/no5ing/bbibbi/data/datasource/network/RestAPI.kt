package com.no5ing.bbibbi.data.datasource.network

import com.no5ing.bbibbi.data.datasource.network.request.member.AddFcmTokenRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.AddPostRealEmojiRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.ChangeNameRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.ChangeProfileImageRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.CreateMemberRealEmojiRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.ImageUploadRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.JoinFamilyRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.QuitMemberRequest
import com.no5ing.bbibbi.data.datasource.network.request.member.UpdateMemberRealEmojiRequest
import com.no5ing.bbibbi.data.datasource.network.request.post.CreatePostCommentRequest
import com.no5ing.bbibbi.data.datasource.network.request.post.CreatePostReactionRequest
import com.no5ing.bbibbi.data.datasource.network.request.post.CreatePostRequest
import com.no5ing.bbibbi.data.datasource.network.request.post.DeletePostReactionRequest
import com.no5ing.bbibbi.data.datasource.network.request.post.UpdatePostCommentRequest
import com.no5ing.bbibbi.data.datasource.network.response.ArrayResponse
import com.no5ing.bbibbi.data.datasource.network.response.DefaultResponse
import com.no5ing.bbibbi.data.datasource.network.response.Pagination
import com.no5ing.bbibbi.data.model.auth.AppVersion
import com.no5ing.bbibbi.data.model.auth.AuthResult
import com.no5ing.bbibbi.data.model.auth.RefreshAuthRequest
import com.no5ing.bbibbi.data.model.auth.RegisterRequest
import com.no5ing.bbibbi.data.model.auth.SocialLoginRequest
import com.no5ing.bbibbi.data.model.family.Family
import com.no5ing.bbibbi.data.model.family.FamilySummary
import com.no5ing.bbibbi.data.model.link.DeepLink
import com.no5ing.bbibbi.data.model.member.ImageUploadLink
import com.no5ing.bbibbi.data.model.member.Member
import com.no5ing.bbibbi.data.model.member.MemberRealEmoji
import com.no5ing.bbibbi.data.model.member.MemberRealEmojiList
import com.no5ing.bbibbi.data.model.mission.Mission
import com.no5ing.bbibbi.data.model.post.CalendarBanner
import com.no5ing.bbibbi.data.model.post.CalendarElement
import com.no5ing.bbibbi.data.model.post.DailyCalendarElement
import com.no5ing.bbibbi.data.model.post.Post
import com.no5ing.bbibbi.data.model.post.PostComment
import com.no5ing.bbibbi.data.model.post.PostReaction
import com.no5ing.bbibbi.data.model.post.PostReactionSummary
import com.no5ing.bbibbi.data.model.post.PostRealEmoji
import com.no5ing.bbibbi.data.model.view.FamilyInviteModel
import com.no5ing.bbibbi.data.model.view.MainPageModel
import com.no5ing.bbibbi.data.model.view.NightMainPageModel
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

interface RestAPI {
    /**
     * 가족 API
     */
    interface FamilyApi {
        @POST("v1/families")
        suspend fun createFamily(): ApiResponse<Family>
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

        @HTTP(method = "DELETE", path = "v1/members/{memberId}", hasBody = true)
        suspend fun quitMember(
            @Path("memberId") memberId: String,
            @Body body: QuitMemberRequest,
        ): ApiResponse<DefaultResponse>

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

        @POST("v1/me/quit-family")
        suspend fun quitFamily(): ApiResponse<DefaultResponse>

        @GET("v1/me/app-version")
        suspend fun getAppVersion(): ApiResponse<AppVersion>

        @POST("v1/members/{memberId}/real-emoji/image-upload-request")
        suspend fun getRealEmojiImageRequest(
            @Path("memberId") memberId: String,
            @Body body: ImageUploadRequest,
        ): ApiResponse<ImageUploadLink>

        @GET("v1/members/{memberId}/real-emoji")
        suspend fun getRealEmojiList(
            @Path("memberId") memberId: String,
        ): ApiResponse<MemberRealEmojiList<MemberRealEmoji>>

        @POST("v1/members/{memberId}/real-emoji")
        suspend fun createMemberRealEmoji(
            @Path("memberId") memberId: String,
            @Body body: CreateMemberRealEmojiRequest,
        ): ApiResponse<MemberRealEmoji>

        @PUT("v1/members/{memberId}/real-emoji/{realEmojiId}")
        suspend fun updateMemberRealEmoji(
            @Path("memberId") memberId: String,
            @Path("realEmojiId") realEmojiId: String,
            @Body body: UpdateMemberRealEmojiRequest,
        ): ApiResponse<MemberRealEmoji>

        @POST("v1/members/{memberId}/pick")
        suspend fun pickMember(
            @Path("memberId") memberId: String,
        ): ApiResponse<DefaultResponse>
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
            @Query("type") type: String? = null,
            @Query("sort") sort: String? = "DESC",
        ): ApiResponse<Pagination<Post>>

        @GET("v1/posts/{postId}")
        suspend fun getPost(
            @Path("postId") postId: String,
        ): ApiResponse<Post>

        @POST("v1/posts")
        suspend fun createPost(
            @Body body: CreatePostRequest,
            @Query("type") type: String? = null,
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

        @GET("v1/calendar/monthly")
        suspend fun getMonthlyCalendar(
            @Query("yearMonth") yearMonth: String,
        ): ApiResponse<ArrayResponse<CalendarElement>>

        @GET("v1/calendar/banner")
        suspend fun getCalendarBanner(
            @Query("yearMonth") yearMonth: String,
        ): ApiResponse<CalendarBanner>

        @GET("v1/calendar/summary")
        suspend fun getFamilySummary(
            @Query("yearMonth") yearMonth: String,
        ): ApiResponse<FamilySummary>

        @GET("v1/calendar/daily")
        suspend fun getDailyCalendar(
            @Query("yearMonthDay") date: LocalDate,
        ): ApiResponse<ArrayResponse<DailyCalendarElement>>

        @GET("v1/posts/{postId}/comments")
        suspend fun getPostComments(
            @Path("postId") postId: String,
            @Query("page") page: Int?,
            @Query("size") size: Int?,
            @Query("sort") sort: String? = "DESC",
        ): ApiResponse<Pagination<PostComment>>

        @DELETE("v1/posts/{postId}/comments/{commentId}")
        suspend fun deletePostComment(
            @Path("postId") postId: String,
            @Path("commentId") commentId: String,
        ): ApiResponse<DefaultResponse>

        @POST("v1/posts/{postId}/comments")
        suspend fun createPostComment(
            @Path("postId") postId: String,
            @Body body: CreatePostCommentRequest,
        ): ApiResponse<PostComment>

        @PUT("v1/posts/{postId}/comments/{commentId}")
        suspend fun updatePostComment(
            @Path("postId") postId: String,
            @Path("commentId") commentId: String,
            @Body body: UpdatePostCommentRequest,
        ): ApiResponse<PostComment>

        @GET("v1/posts/{postId}/real-emoji")
        suspend fun getPostRealEmojiList(
            @Path("postId") postId: String,
        ): ApiResponse<ArrayResponse<PostRealEmoji>>

        @POST("v1/posts/{postId}/real-emoji")
        suspend fun addPostRealEmojiToPost(
            @Path("postId") postId: String,
            @Body body: AddPostRealEmojiRequest,
        ): ApiResponse<PostRealEmoji>

        @DELETE("v1/posts/{postId}/real-emoji/{postRealEmojiId}")
        suspend fun deletePostRealEmojiFromPost(
            @Path("postId") postId: String,
            @Path("postRealEmojiId") postRealEmojiId: String,
        ): ApiResponse<DefaultResponse>

        @GET("v1/missions/today")
        suspend fun getDailyMission(): ApiResponse<Mission>

        @GET("v1/missions/{missionId}")
        suspend fun getMissionById(
            @Path("missionId") missionId: String,
        ): ApiResponse<Mission>
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

    interface ViewApi {
        @GET("v1/view/main/daytime-page")
        suspend fun getMainView(): ApiResponse<MainPageModel>

        @GET("v1/view/main/nighttime-page")
        suspend fun getNightMainView(): ApiResponse<NightMainPageModel>

        @GET("v1/view/family-invite/{linkId}")
        suspend fun getFamilyInviteView(
            @Path("linkId") linkId: String,
        ): ApiResponse<FamilyInviteModel>
    }

    /**
     * API 모음
     */
    fun getMemberApi(): MemberApi
    fun getFamilyApi(): FamilyApi
    fun getPostApi(): PostApi
    fun getAuthApi(): AuthApi
    fun getLinkApi(): LinkApi
    fun getViewApi(): ViewApi
}