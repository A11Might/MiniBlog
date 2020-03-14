var CONTEXT_PATH = "/miniblog";

$(function(){
    $("#publishBtn").click(publish);
});

function publish() {
    // 获取内容
    var content = $("#content").val();
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
                    window.location.reload();
                }
            }, 2000);
        }
    );

}