package com.robin.core.base.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
    public static final List<String> avaiableCompressSuffixs = Arrays.asList(new String[]{Const.CompressType.COMPRESS_TYPE_GZ.toString(), Const.CompressType.COMPRESS_TYPE_LZO.toString(), Const.CompressType.COMPRESS_TYPE_BZ2.toString(), Const.CompressType.COMPRESS_TYPE_SNAPPY.toString(), Const.CompressType.COMPRESS_TYPE_ZIP.toString(), Const.CompressType.COMPRESS_TYPE_LZMA.toString()});
    public static final Const.CompressType[] compressTypeEnum = {Const.CompressType.COMPRESS_TYPE_GZ, Const.CompressType.COMPRESS_TYPE_LZO, Const.CompressType.COMPRESS_TYPE_BZ2, Const.CompressType.COMPRESS_TYPE_SNAPPY, Const.CompressType.COMPRESS_TYPE_ZIP, Const.CompressType.COMPRESS_TYPE_LZMA};

    public static String parseFileFormat(String path, List<String> suffix) {
        String filePath = null;
        if (suffix == null) {
            suffix = new ArrayList<String>();
        }
        if (path != null && !path.trim().isEmpty()) {
            int pos = path.lastIndexOf(File.separator);
            if (pos == -1) {
                pos = path.lastIndexOf("/");
            }
            if (pos != -1) {
                String fileName = path.substring(pos + 1, path.length());
                String[] arr = fileName.split("\\.");
                int lastpos = fileName.lastIndexOf(".");
                filePath = fileName.substring(0, lastpos);
                for (int i = arr.length - 1; i > 0; i--) {
                    suffix.add(arr[i]);
                }
            }
        }
        return filePath;
    }

    public static Const.CompressType getFileCompressType(List<String> suffixList) {
        Const.CompressType type = Const.CompressType.COMPRESS_TYPE_NONE;
        if (!suffixList.isEmpty() && avaiableCompressSuffixs.contains(suffixList.get(0).toLowerCase())) {
            type = compressTypeEnum[avaiableCompressSuffixs.indexOf(suffixList.get(0))];
        }
        return type;
    }
}
