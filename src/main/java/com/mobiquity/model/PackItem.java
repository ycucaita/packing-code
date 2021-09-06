package com.mobiquity.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PackItem {

    private int index;
    private BigDecimal weight;
    private BigDecimal cost;
}
