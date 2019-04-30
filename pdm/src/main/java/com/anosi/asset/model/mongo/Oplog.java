package com.anosi.asset.model.mongo;

import com.alibaba.fastjson.annotation.JSONField;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @Description: 操作日志
 * @Author: zxgang
 * @Date: 2019/1/14 10:45
 */
@Document
public class Oplog extends AbstractDocument {

    private String operator; // 操作人
    private String target;  // 操作对象
    private Date opTime;    // 操作时间
    private Operation operation;   // 操作
    private String content; // 操作内容

    public Oplog(String operator, String target, Operation operation, String content) {
        this.operator = operator;
        this.target = target;
        this.operation = operation;
        this.content = content;
        this.opTime = new Date();
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public enum  Operation {

        ADD("新增"),EDIT("编辑"),DEL("删除"),BATCH_ADD("批量导入"),UPLOAD("上传"),START("发起"),HANDLE("处理");

        private String name;

        Operation(String name) {
            this.name = name;
        }

        public static Operation getByName(String name) {
            for (Operation operation : values()) {
                if (operation.getName().equals(name)) {
                    return operation;
                }
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

}
