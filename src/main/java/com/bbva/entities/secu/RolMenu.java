package com.bbva.entities.secu;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class RolMenu {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("role_id")
    private int roleId;

    @SerializedName("role_name")
    private String roleName;

    @SerializedName("role_desc")
    private String roleDesc;

    @SerializedName("menu_id")
    private int menuId;

    @SerializedName("menu_desc")
    private String menuDesc;

    @SerializedName("menu_icon")
    private String menuIcon;

    @SerializedName("menu_url")
    private String menuUrl;

    @SerializedName("menu_order")
    private int menuOrder;

    @SerializedName("role_menu_id")
    private Integer roleMenuId;

    @SerializedName("menu_parent")
    private int menuParent;

    @SerializedName("menu_parent_desc")
    private String menuParentDesc;

    @SerializedName("menu_icon_parent")
    private String menuIconParent;

    @SerializedName("menu_order_parent")
    private Integer menuOrderParent;
}
