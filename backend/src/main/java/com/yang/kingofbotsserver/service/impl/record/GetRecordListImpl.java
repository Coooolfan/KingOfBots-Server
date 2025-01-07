package com.yang.kingofbotsserver.service.impl.record;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yang.kingofbotsserver.mapper.RecordMapper;
import com.yang.kingofbotsserver.mapper.UserMapper;
import com.yang.kingofbotsserver.pojo.User;
import com.yang.kingofbotsserver.service.record.GetRecordList;
import com.yang.kingofbotsserver.pojo.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class GetRecordListImpl implements GetRecordList {
    private final static Integer pageSize = 5;
    @Autowired
    private RecordMapper recordMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public JSONObject getList(Integer page) {
        IPage<Record> recordIPage = new Page<>(page, pageSize);
        QueryWrapper<Record> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().orderByDesc(Record::getId);
        List<Record> records = recordMapper.selectPage(recordIPage, queryWrapper).getRecords();
        LinkedList<JSONObject> items = new LinkedList<>();
        for (Record record : records) {
            User userA = userMapper.selectById(record.getAId());
            User userB = userMapper.selectById(record.getBId());
            JSONObject item = new JSONObject();
            item.put("a_photo", userA.getPhoto());
            item.put("a_username", userA.getUsername());
            item.put("a_id", userA.getId());
            item.put("b_photo", userB.getPhoto());
            item.put("b_username", userB.getUsername());
            item.put("b_id", userB.getId());
            item.put("record", record);
            String result = "平局";
            if (record.getLoser().equals("a"))
                result = userA.getUsername() + " 获胜";
            if (record.getLoser().equals("b"))
                result = userB.getUsername() + " 获胜";
            item.put("result", result);
            items.add(item);
        }
        JSONObject result = new JSONObject();
        result.put("items", items);
        result.put("records_count", recordMapper.selectCount(null));
        return result;
    }
}
