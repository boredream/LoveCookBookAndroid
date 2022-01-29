package com.boredream.baseapplication.image.upload;

import com.blankj.utilcode.util.CollectionUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

// 该类用于把对象里本地图片地址上传后替换成网络图片地址。
public class ImageRequestUtils {

    static class JeInfo {
        JsonElement parent;
        JsonElement je;
        Object key;

        public JeInfo(JsonElement parent, Object key, JsonElement je) {
            this.parent = parent;
            this.key = key;
            this.je = je;
        }
    }

    public static <T> Observable<T> checkImage4update(T t) {
        Queue<JeInfo> queue = new LinkedList<>();
        JsonElement element = new JsonParser().parse(new Gson().toJson(t));
        queue.add(new JeInfo(null, null, element));

        // 解析数据，记录本地图片字段
        List<JeInfo> localImagePathJeList = new ArrayList<>();
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                JeInfo info = queue.poll();
                if (info == null) continue;
                if (info.je.isJsonArray()) {
                    // 数组循环添加，元素可能是对象也可能是字符串
                    JsonArray array = info.je.getAsJsonArray();
                    for (int index = 0; index < array.size(); index++) {
                        queue.add(new JeInfo(info.je, index, array.get(index)));
                    }
                } else if (info.je.isJsonObject()) {
                    // 对象继续拆变量添加
                    for (Map.Entry<String, JsonElement> entry : info.je.getAsJsonObject().entrySet()) {
                        queue.add(new JeInfo(info.je, entry.getKey(), entry.getValue()));
                    }
                } else if (info.je.isJsonPrimitive() && info.je.getAsJsonPrimitive().isString()) {
                    // 字符串类型
                    String str = info.je.getAsString();
                    // 初步判断，如果是本地图片字段，记录之
                    if (str.contains("/storage/")) {
                        localImagePathJeList.add(info);
                    }
                }
            }
        }

        if(CollectionUtils.isEmpty(localImagePathJeList)) {
            // 无需上传
            return Observable.just(t);
        }

        // 上传本地图片后替换成url
        List<Observable<Object>> obsList = new ArrayList<>();
        for (JeInfo info : localImagePathJeList) {
            for (String path : info.je.getAsString().split(",")) {
                // 可能字段下多个图片，挨个上传逐个替换
                obsList.add(uploadImage(info, path));
            }
        }

        // 合并
        return Observable.zip(obsList, objects -> new Gson().<T>fromJson(element, t.getClass()))
                .observeOn(Schedulers.io());
    }

    private static Observable<Object> uploadImage(JeInfo info, String path) {
        return Observable.create(emitter -> {
            if (!path.contains("/storage/") || !new File(path).exists()) {
                // 进一步判断，不是合法的本地图片地址，不处理
                emitter.onNext(path);
                emitter.onComplete();
                return;
            }

            ImageUploadUtils.startUpload(path, new ImageUploadUtils.OnImageUploadListener() {
                @Override
                public void onProgressChanged(int percentage) {

                }

                @Override
                public void onSuccess(String url) {
                    // 将原path，逐个替换成新url
                    if (info.parent.isJsonObject()) {
                        // 对象，key是字段名
                        JsonObject obj = info.parent.getAsJsonObject();
                        String name = info.key.toString();

                        String oldUrl = obj.get(name).getAsJsonPrimitive().getAsString();
                        String newUrl = oldUrl.replace(path, url);
                        obj.addProperty(name, newUrl);
                    } else if (info.parent.isJsonArray()) {
                        // 数组，key是索引
                        JsonArray array = info.parent.getAsJsonArray();
                        int index = Integer.parseInt(info.key.toString());

                        String oldUrl = array.get(index).getAsJsonPrimitive().getAsString();
                        String newUrl = oldUrl.replace(path, url);
                        array.set(index, new JsonPrimitive(newUrl));
                    }

                    emitter.onNext("");
                    emitter.onComplete();
                }

                @Override
                public void onError(Throwable e) {
                    if (!emitter.isDisposed()) {
                        emitter.onError(e);
                    }
                }
            });
        });
    }

}
