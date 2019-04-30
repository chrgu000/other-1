/**
 *
 */
$(document).ready(function() {
    $("#departmentForm").validate({
        debug:true,
        rules : {
            name : {
                minlength: 1,
                required : true,
                checkUniqueName : true
            },
            code : {
                minlength: 1,
                required : true,
                checkUniqueCode : true,
            },
            depGroup : {
                required : true
            }
        },
        submitHandler: function(form) {
            var options = {
                type : "post",
                url : '/roleDepGroupManage/saveDepartment',
                data : {
                    // 'name': $('#name').val(),
                    // 'code': $('#code').val()
                },
                success : function(data) {
                    $.unblockUI();
                    console.log("result"+data.result);
                    if(data.result=='success'){
                        var func = function(){
                            window.location.href="/roleDepGroupManage/gotoDepartmentList"
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

    /*部门名重复判断*/
    jQuery.validator.addMethod("checkUniqueName", function(value, element) {
        var flag;
        $.ajax({
            url : '/roleDepGroupManage/checkDepartmentExist',
            data : {
                'name' : $("#name").val(),
            },
            type : 'get',
            dataType : 'json',
            async:false,
            success : function(msg) {
                console.log("msg:"+msg);
                if (msg.result == true) {
                    if($("#departmentId").val()!=null&&$("#departmentId").val()!=""){
                        $.ajax({
                            url : '/roleDepGroupManage/checkDepartmentExist',
                            data : {
                                'name' : $("#name").val(),
                                'id' : $("#departmentId").val(),
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
    }, "您输入的部门名已存在");

    /*部门英文名code重复判断*/
    jQuery.validator.addMethod("checkUniqueCode", function(value, element) {
        var flag;
        $.ajax({
            url : '/roleDepGroupManage/checkDepartmentExist',
            data : {
                'code' : $("#code").val(),
            },
            type : 'get',
            dataType : 'json',
            async:false,
            success : function(msg) {
                console.log("msg:"+msg);
                if (msg.result == true) {
                    if($("#departmentId").val()!=null&&$("#departmentId").val()!=""){
                        $.ajax({
                            url : '/roleDepGroupManage/checkDepartmentExist',
                            data : {
                                'code' : $("#code").val(),
                                'id' : $("#departmentId").val(),
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
    }, "您输入的部门英文名已存在");

    /*if($("#departmentId").val()!=null&&$("#departmentId").val()!=""){
        console.log("组id："+$("#depGroupId").val());
        $.each(eval($("#depGroupId").val()),function(index,item){
            $("#depGroup option[value='"+ item.id +"']").attr("selected","selected");
        });
    }

    $("#depGroup").chosen();*/

})
