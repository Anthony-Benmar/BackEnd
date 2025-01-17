package com.bbva.entities.jiravalidator;

import com.bbva.entities.BaseEntity;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class JiraValidatorLogEntity{

    @SerializedName("id")
    private Long id;

    @SerializedName("fecha")
    private LocalDateTime fecha;

    @SerializedName("usuario")
    private String usuario;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("ticket")
    private String ticket;

    @SerializedName("regla_1")
    private String regla1;

    @SerializedName("regla_2")
    private String regla2;

    @SerializedName("regla_3")
    private String regla3;

    @SerializedName("regla_4")
    private String regla4;

    @SerializedName("regla_5")
    private String regla5;

    @SerializedName("regla_6")
    private String regla6;

    @SerializedName("regla_7")
    private String regla7;

    @SerializedName("regla_8")
    private String regla8;

    @SerializedName("regla_9")
    private String regla9;

    @SerializedName("regla_10")
    private String regla10;

    @SerializedName("regla_11")
    private String regla11;

    @SerializedName("regla_12")
    private String regla12;

    @SerializedName("regla_13")
    private String regla13;

    @SerializedName("regla_14")
    private String regla14;

    @SerializedName("regla_15")
    private String regla15;

    @SerializedName("regla_16")
    private String regla16;

    @SerializedName("regla_17")
    private String regla17;

    @SerializedName("regla_18")
    private String regla18;

    @SerializedName("regla_19")
    private String regla19;

    @SerializedName("regla_20")
    private String regla20;

    @SerializedName("regla_21")
    private String regla21;

    @SerializedName("regla_22")
    private String regla22;

    @SerializedName("regla_23")
    private String regla23;

    @SerializedName("regla_24")
    private String regla24;

    @SerializedName("regla_25")
    private String regla25;

    @SerializedName("regla_26")
    private String regla26;

    @SerializedName("regla_27")
    private String regla27;

    @SerializedName("regla_28")
    private String regla28;

    @SerializedName("regla_29")
    private String regla29;

    @SerializedName("regla_30")
    private String regla30;

    @SerializedName("regla_31")
    private String regla31;

    @SerializedName("regla_32")
    private String regla32;

}
