package com.bbva.entities.template;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class Template {
    public Integer template_id;
    public Integer type_id;
    public String label_one;
    public String process_code;
    public String name;
    public String description;
    public Integer status;
    public Integer orden;
    public String fase;
    public String sub_fase;

}
