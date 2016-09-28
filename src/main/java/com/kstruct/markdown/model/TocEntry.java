package com.kstruct.markdown.model;

import java.util.List;

import lombok.Data;

@Data
public class TocEntry {
    private final String label;
    private final int level;

    public String getAnchorId() {
        return label.replaceAll("\\W", "-").replaceAll("-+", "-").toLowerCase();
    }
}