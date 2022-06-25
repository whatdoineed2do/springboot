package com.example.springboot.service;

import com.example.springboot.controller.exception.FooException;
import com.example.springboot.model.Meta;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HelloService {
    private static Map<Long, Meta> helloDB;
    private int  stid;

    @PostConstruct
    public void init() throws Exception {
        helloDB = new HashMap<>();
    }

    public int  addObject(Long o, String blob) throws FooException {
        if (o == 666) {
            return -1;
        }
        Meta  m = getById(o);
        if (m != null) {
            return 1;
        }

        m = new Meta(o, blob);
        helloDB.put(m.getObjectId(), m);
        return 0;
    }

    public void addMeta(Meta m) {
        Meta  im = getById(m.getObjectId());
	if (im != null) {
	}
        helloDB.put(m.getObjectId(), m);
    }

    public List<Meta> getAll(){
        List<Meta> metaList = helloDB.entrySet().stream()
                .map(Map.Entry::getValue).collect(Collectors.toList());
        return metaList;
    }

    public Meta getById(Long id) {
        return helloDB.get(id);
    }
}
