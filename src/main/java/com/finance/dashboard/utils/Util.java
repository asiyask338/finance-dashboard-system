package com.finance.dashboard.utils;

import com.finance.dashboard.enums.RoleName;
import com.finance.dashboard.exception.UnauthorizedException;

public class Util {

	public void checkAdminAccess(String role) {
		if (!role.equals(RoleName.ADMIN.name())) {
			throw new UnauthorizedException("Only ADMIN can perform this action");
		}
	}

	public void checkAdminOrAnalyst(String role) {
		if (!(role.equals(RoleName.ADMIN.name()) || role.equals(RoleName.ANALYST.name()))) {
			throw new UnauthorizedException("Access denied");
		}
	}
}
