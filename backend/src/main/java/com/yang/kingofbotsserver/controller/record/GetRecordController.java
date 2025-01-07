package com.yang.kingofbotsserver.controller.record;

import com.alibaba.fastjson2.JSONObject;
import com.yang.kingofbotsserver.service.record.GetRecordList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GetRecordController {
    private GetRecordList getRecordList;

    @Autowired
    public GetRecordController(GetRecordList getRecordList) {
        this.getRecordList = getRecordList;
    }

    @GetMapping("/api/record/getlist/")
    JSONObject getRecordList(@RequestParam Map<String,String> data) {
        Integer page = Integer.parseInt(data.get("page"));
        return getRecordList.getList(page);
    }
}
