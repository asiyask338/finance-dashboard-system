package com.finance.dashboard.dto.res;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategorySummaryResponse {

	private String category;

	private Double total;
}