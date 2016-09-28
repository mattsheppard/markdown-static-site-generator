package com.kstruct.markdown.model;

import lombok.Data;

@Data
public class TocEntry {
    private final String label;
    private final int index;
    private final int level;
}