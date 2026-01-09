package com.example.demo.model;


import java.util.Map;

public record InputMessage(String id, Map<String,Object> payload) {
    //record: immutable type
}
