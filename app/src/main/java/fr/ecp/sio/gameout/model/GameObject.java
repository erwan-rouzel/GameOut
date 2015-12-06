package fr.ecp.sio.gameout.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import fr.ecp.sio.gameout.utils.GameoutUtils;

/**
 * Created by erwanrouzel on 06/12/2015.
 */
public abstract class GameObject {
    @Override
    public String toString() {
        ArrayList<String> strArray = new ArrayList<String>();
        for(Field field: this.getClass().getFields()) {
            try {
                strArray.add(field.getName() + "=" + field.get(this).toString());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return "{" + this.getClass().getSimpleName() + ": " + GameoutUtils.implode(", ", strArray) + "}";
    }
}

