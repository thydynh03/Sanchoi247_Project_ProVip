package com.example.SanChoi247.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Size {
    private int size_id;
    private String size;
    public Size(int size_id) {
        this.size_id = size_id;
    }
}
