$(function(){
    $("#publishBtn").click(publish);
});

// 监听 publishBtn 按钮，单击时触发 js。（注意这个方法没有参数的出入）
function publish() {
    // 获取内容
    var content = $("#content").val();
    // var converter = new showdown.Converter(); // 初始化转换器
    // var htmlcontent  = converter.makeHtml(content); // 将 MarkDown 转为 html 格式的内容
    // 发送异步请求(POST)
    $.post(
        CONTEXT_PATH + "/post/add",
        {"content":content},
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
                    // window.location.reload();
                    window.location.href="/index";
                }
            }, 2000);
        }
    );
}

// 首页全部的评论按钮都是一个，但需要按楼层给评论框 textarea 取 id（id = "commentContent" + level），这样每次点击按钮运行 js 方法时，
// 可以取到对应楼层微博的评论框中的信息。
function commentPost(btn, postId, level) {
    // 获取评论
    var commentContent = $("#commentContent" + level).val();
    // 发送异步请求(POST)
    $.post(
        CONTEXT_PATH + "/comment/add",
        {"content":commentContent, "postId":postId},
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

function deletePost(btn, postId) {
    // 发送异步请求(POST)
    $.post(
        CONTEXT_PATH + "/post/delete",
        {"postId":postId},
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

// 首页全部的点赞按钮都是一个，当点击按钮运行 js 时，由于出入参数不同，会对不同的实体进行操作。
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