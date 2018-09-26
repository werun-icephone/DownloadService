package com.example.updateservice.util;

import com.example.updateservice.model.type.ErrorType;
import com.example.updateservice.model.type.RequestType;


/**
 * Created by Billy on 2018/4/14.
 *
 * 分析枚举类型
 */

public class ParseTypeUtil {

    public static final int TYPE_NO_FOUND = -10;

    public static int parseErrorType(ErrorType type) {
        switch (type) {
            case IO:
                return 0;
            case DirectoryNoFound:
                return 1;
        }
        return TYPE_NO_FOUND;
    }

    public static String parseRequestType(RequestType type) {
        switch (type) {
            case Get:
                return "GET";
            case Post:
                return "POST";
        }

        return null;
    }


}
