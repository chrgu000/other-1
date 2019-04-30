/**
 *
 */
$(document).ready(function() {
    $("#depGroupForm").validate({
        debug:true,
        rules : {
            name : {
                minlength: 1,
                required : true
                //checkUniqueName : true,
            },
            code : {
                minlength: 1,
                required : true,
                checkUniqueCode : true,
            },
            role : {
                required : true
                //checkUniqueRole : true
            },
            department : {
                required : true
            }
        },
        submitHandler: function(form) {
            var options = {
                type : "post",
                url : '/roleDepGroupManage/saveDepGroup',
                data : {
                    // 'name': $('#name').val(),
                    // 'code': $('#code').val()
                },
                success : function(data) {
                    $.unblockUI();
                    console.log("result"+data.result);
                    if(data.result=='success'){
                        var func = function(){
                            window.location.href="/roleDepGroupManage/gotoDepGroupList"
                        }
                        infoAndFunc('操作成功',func);
                    }else if(data.result=='error'){
                        warning('操作失败:'+data.message);
                    }else{
                        warning('操作失败');
                    }
                }
            };

            $.blockUI({
                message: '<div class="lds-css ng-scope"><div class="lds-spinner" style="100%;height:100%"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div></div>',
                // 指的是提示框的css
                css: {
                    width: "0px",
                    top: "40%",
                    left: "50%"
                },
            });
            $(form).ajaxSubmit(options);
        }
    });

    /*组名重复判断*/
    jQuery.validator.addMethod("checkUniqueName", function(value, element) {
        var flag;
        $.ajax({
            url : '/roleDepGroupManage/checkDepGroupExist',
            data : {
                'name' : $("#name").val(),
            },
            type : 'get',
            dataType : 'json',
            async:false,
            success : function(msg) {
                console.log("msg:"+msg);
                if (msg.result == true) {
                    if($("#depGroupId").val()!=null&&$("#depGroupId").val()!=""){
                        $.ajax({
                            url : '/roleDepGroupManage/checkDepGroupExist',
                            data : {
                                'name' : $("#name").val(),
                                'id' : $("#depGroupId").val(),
                            },
                            type : 'get',
                            dataType : 'json',
                            async:false,
                            success : function(msg) {
                                // true表示已经存在
                                if (msg.result == true) {
                                    flag = true;
                                } else{
                                    flag = false;
                                }
                            }
                        });
                    }else{
                        flag = false;
                    }
                }else{
                    flag = true;
                }
            }
        });
        return flag;
    }, "您输入的组名已存在");

    /*组英文名code重复判断*/
    jQuery.validator.addMethod("checkUniqueCode", function(value, element) {
        var flag;
        $.ajax({
            url : '/roleDepGroupManage/checkDepGroupExist',
            data : {
                'code' : $("#code").val(),
            },
            type : 'get',
            dataType : 'json',
            async:false,
            success : function(msg) {
                console.log("msg:"+msg);
                if (msg.result == true) {
                    if($("#depGroupId").val()!=null&&$("#depGroupId").val()!=""){
                        $.ajax({
                            url : '/roleDepGroupManage/checkDepGroupExist',
                            data : {
                                'code' : $("#code").val(),
                                'id' : $("#depGroupId").val(),
                            },
                            type : 'get',
                            dataType : 'json',
                            async:false,
                            success : function(msg) {
                                // true表示已经存在
                                if (msg.result == true) {
                                    flag = true;
                                } else{
                                    flag = false;
                                }
                            }
                        });
                    }else{
                        flag = false;
                    }
                }else{
                    flag = true;
                }
            }
        });
        return flag;
    }, "您输入的组英文名已存在");


    if($("#depGroupId").val()!=null&&$("#depGroupId").val()!=""){
        /*角色多个*/
       /* $.each(eval($("#roleId").val()),function(index,item){
            $("#role option[value='"+ item.id +"']").attr("selected","selected");
        });*/
        /*部门单个*/
        console.log("部门id:"+$("#departmentId").val());
        $("#department option[value='"+ $("#departmentId").val() +"']").attr("selected","selected");
    }

    $("#role").chosen();
    $("#department").chosen();

})
