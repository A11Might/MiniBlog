$(function(){
    $("#followBtn").click(follow);
});

// 监听 followBtn 按钮，单击时触发 js。（注意这个方法没有参数的出入）
function follow() {
    // 获取内容
    var btn = this;
    if($(btn).hasClass("btn-primary")) {
        // 关注
        $.post(
            CONTEXT_PATH + 	"/follow",
            {"entityType":3,"entityId":$(btn).prev().val()},
            function (data) {
                data = $.parseJSON(data);
                if (data.code == 0) {
                    window.location.reload();
                } else {
                    alert(data.msg);
                }
            }
        );
    } else {
        // 取消关注
        $.post(
            CONTEXT_PATH + 	"/unfollow",
            {"entityType":3,"entityId":$(btn).prev().val()},
            function (data) {
                data = $.parseJSON(data);
                if (data.code == 0) {
                    window.location.reload();
                } else {
                    alert(data.msg);
                }
            }
        );
    }
}