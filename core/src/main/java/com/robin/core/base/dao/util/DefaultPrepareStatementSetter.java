package com.robin.core.base.dao.util;

import com.robin.core.base.model.BaseObject;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;


public class DefaultPrepareStatementSetter implements PreparedStatementSetter {
    private List<AnnotationRetrevior.FieldContent> fields;
    private BaseObject object;

    public DefaultPrepareStatementSetter(List<AnnotationRetrevior.FieldContent> fields,  BaseObject object) {

        this.fields = fields;
        this.object = object;
    }

    @Override
    public void setValues(PreparedStatement ps) throws SQLException {
        int pos = 1;
        try {
            for (AnnotationRetrevior.FieldContent field : fields) {
                Object value = field.getGetMethod().invoke(object, new Object[]{});
                if (!field.isIncrement() && value != null) {
                    AnnotationRetrevior.setParameter(ps, pos, value);
                    pos++;
                }
            }
        } catch (Exception ex) {
            throw new SQLException(ex);
        }
    }
}
