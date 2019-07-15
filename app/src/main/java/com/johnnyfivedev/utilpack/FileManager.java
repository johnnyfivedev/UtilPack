package com.johnnyfivedev.utilpack;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.text.format.Formatter;
import android.util.Pair;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.os.EnvironmentCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

import javax.annotation.Nonnull;


public class FileManager {

    private static final String DEFAULT_FILE_NAME_PREFIX = "doctors_handbook";
    private static final String TEMP_JPG_FILE_NAME = "temp.jpg";

    private final Context context;


    public FileManager(Context context) {
        this.context = context;
    }

    // fixme creates only jpg copies
    public File createFileCopy(String pathToUploadAvatarFile) {
        try {
            final File file = new File(context.getCacheDir(), TEMP_JPG_FILE_NAME);
            file.createNewFile();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeFile(pathToUploadAvatarFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80 /*ignored for PNG*/, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArray);
            fileOutputStream.flush();
            fileOutputStream.close();
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * @return path if has enough required free space,
     * else - null
     */
    public String getAvailableFolderPath(@NonNull List<Pair<String, Long>> storagesHolder, long requiredFreeSpaceBytes) {
        List<File> externalDirs = new ArrayList<>();
        File[] files = ContextCompat.getExternalFilesDirs(context, null);
        for (File file : files) {
            if (file != null && EnvironmentCompat.getStorageState(file).equals(Environment.MEDIA_MOUNTED)) {
                long fileFreeSpace = file.getFreeSpace();
                storagesHolder.add(Pair.create(file.getAbsolutePath(), fileFreeSpace));
                if (fileFreeSpace > requiredFreeSpaceBytes) {
                    externalDirs.add(file);
                }
            }
        }
        if (!externalDirs.isEmpty()) {
            File mostFreeExtDir = Collections.max(externalDirs, (o1, o2) -> Long.compare(o1.getFreeSpace(), o2.getFreeSpace()));
            return mostFreeExtDir.getAbsolutePath() + "/databases/";
        }

        File internalDir = context.getFilesDir();
        if (internalDir != null) {
            long internalDirFreeSpace = internalDir.getFreeSpace();
            storagesHolder.add(Pair.create(internalDir.getAbsolutePath(), internalDirFreeSpace));
            if (internalDirFreeSpace > requiredFreeSpaceBytes) {
                return internalDir.getAbsolutePath() + "/databases/";
            }
        }

        return null;
    }

    // https://stackoverflow.com/a/31691791/6325722
    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver contentResolver = context.getContentResolver();
            mimeType = contentResolver.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public String getMimeType(@NonNull File file) {
        String type = null;
        final String url = file.toString();
        final String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        if (type == null) {
            type = "*/*";
        }
        return type;
    }

    public String getBaseMimeType(@NonNull File file) {
        return extractBaseFromMimeType(getMimeType(file));
    }

    public String extractBaseFromMimeType(String mimeType) {
        String baseMimeType = mimeType.split("/")[0];
        return baseMimeType + "/*";
    }

    public String getFileExtension(Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());
        }

        return extension;
    }

    public String createFileNameWithTimestamp(String prefix, String extension) {
        return prefix + System.currentTimeMillis() + "." + extension;
    }

    public String generateDefaultFileName(String extension) {
        return DEFAULT_FILE_NAME_PREFIX + UUID.randomUUID().toString() + "." + extension;
    }

    public File createFromUri(@NonNull Uri uri) {
        return new File(uri.getPath());
    }

    /**
     * @return file length in bytes
     */
    public long getFileSizeBytes(@Nullable File file) {
        if (file == null) {
            return 0;
        }

        return file.length();
    }

    public String getFileSizeFormatted(@Nullable File file) {
        if (file == null) {
            return Formatter.formatFileSize(context, 0);
        }

        return Formatter.formatFileSize(context, file.length());
    }

    public String getFileSizeFormatted(long sizeBytes) {
        return Formatter.formatFileSize(context, sizeBytes);
    }

    public File createFileInExternalStorage(String extension) {
        return new File(Environment.getExternalStorageDirectory(), generateDefaultFileName(extension));
    }

    public File createFileInExternalStorage(String namePrefix, String extension) {
        return new File(Environment.getExternalStorageDirectory(), createFileNameWithTimestamp(namePrefix, extension));
    }

    @Nullable
    public File createFileInExternalCache(String name, String extension) {
        // Этот способ создает в папке cache видимый файл, но приписывает ему сгенерированный постфикс
        /* File file = null;
        try {
            file = File.createTempFile(name + "_", "." + extension, context.getExternalCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;*/

        // Этот способ создает в папке cache/*сгенерированное имя* невидимый файл
        /*File file = new File(Files.createTempDir(), name + "." + extension);
        try {
            if (file.createNewFile()) {
                return file;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;*/

        // Этот способ создает в папке cache видимый файл
        File file = new File(context.getExternalCacheDir(), name + "." + extension);
        try {
            if (file.createNewFile()) {
                return file;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    public File createTempFileInCacheDir(@Nonnull Uri fileUri) {
        InputStream in = null;
        OutputStream out = null;
        File result = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && isVirtualFile(fileUri)) {
                in = getInputStreamForVirtualFile(fileUri, "*/*");
            } else {
                ContentResolver contentResolver = context.getContentResolver();
                in = contentResolver.openInputStream(fileUri);
            }

            File saveDirectory = new File(context.getExternalCacheDir(), "temp");
            if (!saveDirectory.exists()) {
                saveDirectory.mkdirs();
            }

            String fileName = fixFileName(getFileName(fileUri));
            result = new File(saveDirectory, fileName);
            out = new FileOutputStream(result);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public String fixFileName(@Nullable String fileName) {
        if (fileName == null) {
            return fileName;
        }
        //remove invalid characters
        //private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t', '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };
        //original regexp
        //[\/\n\r\t\f\`\?\*\\\<\>\|\":]
        String result = fileName.replaceAll("[\\/\\n\\r\\t\\f\\`\\?\\*\\\\\\<\\>\\|\\\":]", "_");

        // cut file name to 127 chars or FileInputStream throws FileNotFoundException
        int javaCharSize = 2;
        int validFileNameLength = 255 / javaCharSize - 1;
        if (fileName.length() >= validFileNameLength) {
            result = result.substring(0, 255 / javaCharSize - 1);
        }
        return result;
    }

    public boolean deleteFile(Uri uri) {
        if (uri != null && uri.getPath() != null) {
            return new File(uri.getPath()).delete();
        }
        return false;
    }

    public boolean deleteFile(File file) {
        if (file != null) {
            return file.delete();
        }
        return false;
    }

    public boolean deleteFile(String filePath) {
        if (filePath != null) {
            return new File(filePath).delete();
        }
        return false;
    }

    // https://stackoverflow.com/a/25005243/6325722
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    public String getFileName(@Nullable File file) {
        if (file == null) {
            return "";
        }
        int index = file.getName().lastIndexOf('/');
        return file.getName().substring(index + 1);
    }

   /* public void writeToEndOfFile(String filePath, String content) {
        try {
            FileWriter fileWriter = new FileWriter(filePath, true);
            fileWriter.write("\n" + DateTime.now(TimeZone.getDefault()).format("YYYY-MM-DD hh:mm:ss", Locale.getDefault()) + "\t" + content + "\n");
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public void clearFile(String filePath) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write("");
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isImage(String extension) {
        return FileExtensions.Image.containsIgnoreCase(extension);
    }

    public boolean isFileExists(String fullFilePath) {
        File file = new File(fullFilePath);
        return file.isFile();
    }

    public boolean isDirectoryExists(String fullFilePath) {
        File file = new File(fullFilePath);
        return file.isDirectory();
    }

    public boolean createDirIfNotExists(String fillDirPath) {
        File directory = new File(fillDirPath);
        return directory.mkdir();
    }

    //region ===================== Internal ======================

    //https://stackoverflow.com/a/53707346/6325722
    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean isVirtualFile(Uri uri) {
        if (!DocumentsContract.isDocumentUri(context, uri)) {
            return false;
        }

        Cursor cursor = context.getContentResolver().query(
                uri,
                new String[]{DocumentsContract.Document.COLUMN_FLAGS},
                null, null, null);

        int flags = 0;
        if (cursor.moveToFirst()) {
            flags = cursor.getInt(0);
        }
        cursor.close();

        return (flags & DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT) != 0;
    }

    private InputStream getInputStreamForVirtualFile(Uri uri, String mimeTypeFilter) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        String[] openableMimeTypes = contentResolver.getStreamTypes(uri, mimeTypeFilter);
        if (openableMimeTypes == null || openableMimeTypes.length < 1) {
            throw new FileNotFoundException();
        }
        return contentResolver
                .openTypedAssetFileDescriptor(uri, openableMimeTypes[0], null)
                .createInputStream();
    }

    //endregion


    public static abstract class FileExtensions {

        public enum Image {
            JPG("jpg"),
            PNG("png"),
            BMP("bmp"),
            GIF("gif");

            String value;


            Image(String value) {
                this.value = value;
            }

            @Override
            public String toString() {
                return value;
            }

            public static List<String> asList() {
                List<String> list = new ArrayList<>();
                for (Image value : values()) {
                    list.add(value.toString());
                }
                return list;
            }

            public static boolean containsIgnoreCase(String extension) {
                for (Image value : values()) {
                    if (value.toString().equalsIgnoreCase(extension)) {
                        return true;
                    }
                }
                return false;
            }
        }

        public static final String TXT = "txt";
    }

    public static class CreateFileAsyncTask extends AsyncTask<Uri, Void, File> {

        private final FileManager fileManager;
        private final Callback callback;


        public CreateFileAsyncTask(FileManager fileManager, Callback callback) {
            this.fileManager = fileManager;
            this.callback = callback;
        }

        @Override
        protected File doInBackground(Uri... uris) {
            return fileManager.createTempFileInCacheDir(uris[0]);
        }

        @Override
        protected void onPostExecute(File result) {
            if (callback != null) {
                callback.onResult(result);
            }
        }

        public interface Callback {
            void onResult(File file);
        }
    }
}
