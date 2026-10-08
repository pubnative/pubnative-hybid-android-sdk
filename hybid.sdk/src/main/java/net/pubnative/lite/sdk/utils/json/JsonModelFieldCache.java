// HyBid SDK License
//
// https://github.com/pubnative/pubnative-hybid-android-sdk/blob/main/LICENSE
//
package net.pubnative.lite.sdk.utils.json;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by erosgarciaponte on 17.01.18.
 *
 * Per-class descriptor cache for {@link JsonModel}: holds the resolved {@link Field} objects with
 * {@code setAccessible(true)} already applied, keeping the reflection work off the deserialization
 * hot path. Keyed on {@link Class} and backed by a {@link ConcurrentHashMap} for lock-free reads.
 */
public class JsonModelFieldCache {

    // A bound field: the reflected Field (already accessible) plus its metadata.
    static final class BoundField {
        final Field field;
        final JsonModel.JsonModelMetadata meta;

        BoundField(Field field, JsonModel.JsonModelMetadata meta) {
            this.field = field;
            this.meta = meta;
        }
    }

    // Builds a class descriptor; implemented by JsonModel, invoked once per class by getOrBuild.
    interface Builder {
        List<BoundField> build(Class<?> model);
    }

    private static final ConcurrentHashMap<Class<?>, List<BoundField>> cachedModels = new ConcurrentHashMap<>();

    // Lock-free cache read for the hot path; null if not yet built.
    static List<BoundField> get(Class<?> model) {
        return cachedModels.get(model);
    }

    // Returns the descriptor, building it once per class if absent. Synchronized so a concurrent
    // cold start builds only once; callers try get() first so only the miss path takes the lock.
    static synchronized List<BoundField> getOrBuild(Class<?> model, Builder builder) {
        List<BoundField> existing = cachedModels.get(model);
        if (existing != null) {
            return existing;
        }
        List<BoundField> built = builder.build(model);
        cachedModels.put(model, built);
        return built;
    }
}
