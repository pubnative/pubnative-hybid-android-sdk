package net.pubnative.lite.sdk.tracking;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by erosgarciaponte on 12.02.18.
 */

public class MetaData extends Observable implements JsonStream.Streamable {
    private static final String FILTERED_PLACEHOLDER = "[FILTERED]";
    private static final String OBJECT_PLACEHOLDER = "[OBJECT]";

    private String[] filters;
    final Map<String, Object> store;

    public MetaData() {
        store = new ConcurrentHashMap<>();
    }

    public MetaData(Map<String, Object> map) {
        store = new ConcurrentHashMap<>(map);
    }

    @Override
    public void toStream(JsonStream writer) throws IOException {
        objectToStream(store, writer);
    }

    public void addToTab(String tabName, String key, Object value) {
        addToTab(tabName, key, value, true);
    }

    void addToTab(String tabName, String key, Object value, boolean notify) {
        Map<String, Object> tab = getTab(tabName);

        if (value != null) {
            tab.put(key, value);
        } else {
            tab.remove(key);
        }

        notifyHyBidObservers(NotifyType.META);
    }

    public void clearTab(String tabName) {
        store.remove(tabName);

        notifyHyBidObservers(NotifyType.META);
    }

    Map<String, Object> getTab(String tabName) {
        Map<String, Object> tab = (Map<String, Object>) store.get(tabName);

        if (tab == null) {
            tab = new ConcurrentHashMap<>();
            store.put(tabName, tab);
        }

        return tab;
    }

    void setFilters(String... filters) {
        this.filters = filters;

        notifyHyBidObservers(NotifyType.FILTERS);
    }

    static MetaData merge(MetaData... metaDataList) {
        List<Map<String, Object>> stores = new ArrayList<>();
        List<String> filters = new ArrayList<>();
        for (MetaData metaData : metaDataList) {
            if (metaData != null) {
                stores.add(metaData.store);

                if (metaData.filters != null) {
                    filters.addAll(Arrays.asList(metaData.filters));
                }
            }
        }

        MetaData newMeta = new MetaData(mergeMaps(stores.toArray(new Map[0])));
        newMeta.filters = filters.toArray(new String[filters.size()]);

        return newMeta;
    }

    private static Map<String, Object> mergeMaps(Map<String, Object>... maps) {
        Map<String, Object> result = new ConcurrentHashMap<>();

        for (Map<String, Object> map : maps) {
            if (map == null) {
                continue;
            }

            // Get a set of all possible keys in base and overrides
            Set<String> allKeys = new HashSet<>(result.keySet());
            allKeys.addAll(map.keySet());

            for (String key : allKeys) {
                Object baseValue = result.get(key);
                Object overridesValue = map.get(key);

                if (overridesValue != null) {
                    if (baseValue != null
                            && baseValue instanceof Map
                            && overridesValue instanceof Map) {
                        // Both original and overrides are Maps, go deeper
                        result.put(key, mergeMaps((Map<String, Object>) baseValue,
                                (Map<String, Object>) overridesValue));
                    } else {
                        result.put(key, overridesValue);
                    }
                } else {
                    // No collision, just use base value
                    result.put(key, baseValue);
                }
            }
        }

        return result;
    }

    // Write complex/nested values to a JsonStreamer
    private void objectToStream(Object obj,
                                JsonStream writer) throws IOException {
        if (obj == null) {
            writer.nullValue();
        } else if (obj instanceof String) {
            writer.value((String) obj);
        } else if (obj instanceof Number) {
            writer.value((Number) obj);
        } else if (obj instanceof Boolean) {
            writer.value((Boolean) obj);
        } else if (obj instanceof Map) {
            // Map objects
            writer.beginObject();
            for (Object o : ((Map) obj).entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                Object keyObj = entry.getKey();
                if (keyObj instanceof String) {
                    String key = (String) keyObj;
                    writer.name(key);
                    if (shouldFilter(key)) {
                        writer.value(FILTERED_PLACEHOLDER);
                    } else {
                        objectToStream(entry.getValue(), writer);
                    }
                }
            }
            writer.endObject();
        } else if (obj instanceof Collection) {
            // Collection objects (Lists, Sets etc)
            writer.beginArray();
            for (Object entry : (Collection) obj) {
                objectToStream(entry, writer);
            }
            writer.endArray();
        } else if (obj.getClass().isArray()) {
            // Primitive array objects
            writer.beginArray();
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i += 1) {
                objectToStream(Array.get(obj, i), writer);
            }
            writer.endArray();
        } else {
            writer.value(OBJECT_PLACEHOLDER);
        }
    }

    // Should this key be filtered
    private boolean shouldFilter(String key) {
        if (filters == null || key == null) {
            return false;
        }

        for (String filter : filters) {
            if (key.contains(filter)) {
                return true;
            }
        }

        return false;
    }

    private void notifyHyBidObservers(NotifyType type) {
        setChanged();
        super.notifyObservers(type.getValue());
    }
}
