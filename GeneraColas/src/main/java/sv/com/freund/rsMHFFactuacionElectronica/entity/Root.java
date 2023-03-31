/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sv.com.freund.rsMHFFactuacionElectronica.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author Sault
 */
@Data
public class Root {
    public String id;
    public Description description;
    public boolean active;
    public CloseContent closeContent;
    public String title;
    public String htmlIntructions;

    @lombok.Data
    public static class CloseContent {
        public String text;
        public String multimediaUrl;
        public String multimediaType;
    }

    @lombok.Data
    public static class Description {
        public String name;
        public String description;
    }

}
