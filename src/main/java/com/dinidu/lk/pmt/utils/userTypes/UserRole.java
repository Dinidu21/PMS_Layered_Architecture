package com.dinidu.lk.pmt.utils.userTypes;

import lombok.Getter;
@Getter
public enum UserRole {
    ADMIN(1),
    PROJECT_MANAGER(2),
    PRODUCT_OWNER(3),
    TECH_LEAD(4),
    DEVELOPER(5),
    QA_LEAD(6),
    SCRUM_MASTER(7),
    UX_UI_DESIGNER(8),
    DEVOPS_ENGINEER(9),
    DATA_ANALYST(10);

    private final int id;

    UserRole(int id) {
        this.id = id;
    }

}
