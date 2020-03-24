$(function(){
    $("#commentBtn").click(comment);
    $("#deleteBtn").click(deletePost);
});

function comment() {
    // 获取内容
    var postId = $("#postId").val();
    var content = $("#content").val();
    // 发送异步请求(POST)
    $.post(
        CONTEXT_PATH + "/comment/add/",
            {"content":content, "postId":postId},
            function(data) {
            data = $.parseJSON(data);
            // 在提示框中显示返回消息
            $("#hintBody").text(data.msg);
            // 显示提示框
            $("#hintModal").modal("show");
            // 2秒后,自动隐藏提示框
            setTimeout(function(){
                $("#hintModal").modal("hide");
                // 刷新页面
                if(data.code == 0) {
                    window.location.reload();
                }
            }, 2000);
        }
    );
}

function deletePost() {
    var postId = $("#postId").val();
    $.post(
        CONTEXT_PATH + "/post/delete",
        {"postId":postId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                // 浏览器弹出错误信息
                alert(data.msg);
            }
        }
    );
}

function deleteComment(btn, commentId) {
    // 发送异步请求(POST)
    $.post(
        CONTEXT_PATH + "/comment/delete",
        {"commentId":commentId},
        function(data) {
            data = $.parseJSON(data);
            // 在提示框中显示返回消息
            $("#hintBody").text(data.msg);
            // 显示提示框
            $("#hintModal").modal("show");
            // 2秒后,自动隐藏提示框
            setTimeout(function(){
                $("#hintModal").modal("hide");
                // 刷新页面
                if(data.code == 0) {
                    window.location.reload();
                }
            }, 2000);
        }
    );
}

function like(btn, entityType, entityId, entityOwnerId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId,"entityOwnerId":entityOwnerId,"postId":postId},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus?'已赞':"赞");
            } else {
                alert(data.msg);
            }
        }
    );
}