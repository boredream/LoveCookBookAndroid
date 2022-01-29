package com.boredream.baseapplication.net;

import com.boredream.baseapplication.base.BaseResponse;
import com.boredream.baseapplication.entity.Diary;
import com.boredream.baseapplication.entity.FeedBack;
import com.boredream.baseapplication.entity.LoginRequest;
import com.boredream.baseapplication.entity.TheDay;
import com.boredream.baseapplication.entity.Todo;
import com.boredream.baseapplication.entity.TodoGroup;
import com.boredream.baseapplication.entity.User;
import com.boredream.baseapplication.entity.VerifyCodeRequest;
import com.boredream.baseapplication.entity.dto.FileUploadPolicyDTO;
import com.boredream.baseapplication.entity.dto.PageResultDTO;
import com.boredream.baseapplication.entity.dto.WxLoginDTO;

import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {

    /**
     * 获取上传凭证
     */
    @GET("/api/file/getUploadPolicy")
    Observable<BaseResponse<FileUploadPolicyDTO>> getFileGetUploadPolicy();

    /**
     * 上传图片
     */
    @Multipart
    @POST
    Observable<HashMap<String, String>> uploadFile7niu(
            @Url String url,
            @Part MultipartBody.Part file,
            @Part("token") RequestBody token,
            @Part("key") RequestBody key);

    /**
     * 添加日记
     */
    @POST("/api/diary")
    Observable<BaseResponse<String>> postDiary(
            @Body Diary info);

    /**
     * 按月查询日记
     */
    @GET("/api/diary/month")
    Observable<BaseResponse<PageResultDTO<Diary>>> getDiaryMonth(
            @Query("queryDate") String queryDate,
            @Query("page") int page,
            @Query("size") int size);

    /**
     * 分页查询日记
     */
    @GET("/api/diary/page")
    Observable<BaseResponse<PageResultDTO<Diary>>> getDiaryPage(
            @Query("page") int page,
            @Query("size") int size);

    /**
     * 修改日记
     */
    @PUT("/api/diary/{id}")
    Observable<BaseResponse<String>> putDiary(
            @Path("id") Long id,
            @Body Diary info);

    /**
     * 删除日记
     */
    @DELETE("/api/diary/{id}")
    Observable<BaseResponse<String>> deleteDiary(
            @Path("id") Long id);

    /**
     * 添加纪念日
     */
    @POST("/api/the_day")
    Observable<BaseResponse<String>> postTheDay(
            @Body TheDay info);

    /**
     * 分页查询纪念日
     */
    @GET("/api/the_day/page")
    Observable<BaseResponse<PageResultDTO<TheDay>>> getTheDayPage(
            @Query("page") int page,
            @Query("size") int size);

    /**
     * 修改纪念日
     */
    @PUT("/api/the_day/{id}")
    Observable<BaseResponse<String>> putTheDay(
            @Path("id") Long id,
            @Body TheDay info);

    /**
     * 删除纪念日
     */
    @DELETE("/api/the_day/{id}")
    Observable<BaseResponse<String>> deleteTheDay(
            @Path("id") Long id);

    /**
     * 查询所有清单
     */
    @GET("/api/todo")
    Observable<BaseResponse<List<Todo>>> getTodo();

    /**
     * 添加清单
     */
    @POST("/api/todo")
    Observable<BaseResponse<String>> postTodo(
            @Body Todo info);

    /**
     * 修改清单
     */
    @PUT("/api/todo/{id}")
    Observable<BaseResponse<String>> putTodo(
            @Path("id") Long id,
            @Body Todo info);

    /**
     * 删除清单
     */
    @DELETE("/api/todo/{id}")
    Observable<BaseResponse<String>> deleteTodo(
            @Path("id") Long id);

    /**
     * 查询所有清单组
     */
    @GET("/api/todo_group")
    Observable<BaseResponse<List<TodoGroup>>> getTodoGroup();

    /**
     * 添加清单组
     */
    @POST("/api/todo_group")
    Observable<BaseResponse<String>> postTodoGroup(
            @Body TodoGroup info);

    /**
     * 修改清单组
     */
    @PUT("/api/todo_group/{id}")
    Observable<BaseResponse<String>> putTodoGroup(
            @Path("id") Long id,
            @Body TodoGroup info);

    /**
     * 删除清单组
     */
    @DELETE("/api/todo_group/{id}")
    Observable<BaseResponse<String>> deleteTodoGroup(
            @Path("id") Long id);

    /**
     * 添加意见反馈
     */
    @POST("/api/feed_back")
    Observable<BaseResponse<String>> postFeedBack(
            @Body FeedBack info);

    /**
     * 绑定伴侣
     */
    @PUT("/api/user/cp/{cpUserId}")
    Observable<BaseResponse<User>> bindUserCp(
            @Path("cpUserId") Long cpUserId);

    /**
     * 解绑伴侣
     */
    @DELETE("/api/user/cp/{cpUserId}")
    Observable<BaseResponse<Boolean>> unbindUserCp(
            @Path("cpUserId") Long cpUserId);

    /**
     * getUserInfo
     */
    @GET("/api/user/info")
    Observable<BaseResponse<User>> getUserInfo();

    /**
     * login
     */
    @POST("/api/user/login")
    Observable<BaseResponse<String>> postUserLogin(
            @Body LoginRequest info);

    /**
     * register
     */
    @POST("/api/user/register")
    Observable<BaseResponse<String>> postUserRegister(
            @Body LoginRequest info);

    /**
     * wxLogin
     */
    @POST("/api/user/wxlogin")
    Observable<BaseResponse<String>> postUserWxlogin(
            @Body WxLoginDTO info);

    /**
     * 发送验证码
     */
    @GET("/api/user/sendVerifyCode")
    Observable<BaseResponse<Boolean>> sendVerifyCode(
            @Query("phone") String phone);

    /**
     * 使用验证码注册或登录用户
     */
    @POST("/api/user/loginWithVerifyCode")
    Observable<BaseResponse<String>> loginWithVerifyCode(
            @Body VerifyCodeRequest info);

    /**
     * 修改用户
     */
    @PUT("/api/user/{id}")
    Observable<BaseResponse<String>> putUser(
            @Path("id") Long id,
            @Body User info);

    /**
     * 设置密码
     */
    @PUT("/api/user/setPassword")
    Observable<BaseResponse<Boolean>> setPassword(
            @Body HashMap<String, String> info);

}
