package com.example.demo.model;

import java.util.Map;

public record OutputMessage(String id, Map<String,Object> payload) {
    //record: immutable type
}
