package net.mcbbs.client.fixer;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.mcbbs.client.fixer.util.IOUtils;
import net.mcbbs.client.fixer.util.MessageDigestUtils;
import net.mcbbs.client.fixer.util.Tuple;

import java.io.*;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @author yinyangshi InitAuther97
 */
public class DataFixer {
    private JsonReader pullMD5Json(String loc) throws IOException {
        return new JsonReader(new BufferedReader(new InputStreamReader(IOUtils.from(loc == null ? "http://langyo.github.io/MCBBS-Client/update.json" : loc))));
    }

    public void fixUpData(String loc, String root, boolean fixUpCodes, boolean fixUpResources) throws IOException {
        try (JsonReader jr = pullMD5Json(loc)) {
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(jr);
            JsonArray array = je.getAsJsonArray();
            Iterator<JsonElement> iter = array.iterator();
            Map<String, Tuple<String, Tuple<String, String>>> fileMD5 = Maps.newHashMap();
            JsonObject kv;
            while (iter.hasNext()) {
                kv = iter.next().getAsJsonObject();
                fileMD5.put(kv.get("file").getAsString(), Tuple.asTuple(kv.get("md5").getAsString(), Tuple.asTuple(kv.get("path").getAsString(), kv.get("dest").getAsString())));
            }
//            Map<String, String> localFileMD5 = Maps.newHashMap(); unused map, What does it do??
            Path rootPath = Paths.get(
                    Objects.requireNonNull(new File("..").getParentFile().listFiles((dir, name) -> name.contentEquals("scripts")))[0].getAbsolutePath(),
                    "scripts"
            );
            Files.walk(rootPath, FileVisitOption.FOLLOW_LINKS).filter(path -> fileMD5.keySet().contains(path.getFileName().toString()))
                    .map(path -> {
                        try {
                            return Tuple.asTuple(path, MessageDigestUtils.md5(Files.newInputStream(path)));
                        } catch (NoSuchAlgorithmException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }).filter(t -> fileMD5.values()
                    .stream()
                    .map(Tuple::getV1)
                    .noneMatch(p -> p.contentEquals(t.getV1().toFile().getName()))
            ).forEach(t -> {
                try {
                    IOUtils.bindStream(
                            new FileOutputStream(t.getV1().toFile().getName()),
                            IOUtils.from(fileMD5.get(t.getV1().toFile().getName()).getV2().getV1())
                    ).transformAllAndClose();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
