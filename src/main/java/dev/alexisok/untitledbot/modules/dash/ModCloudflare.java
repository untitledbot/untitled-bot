package dev.alexisok.untitledbot.modules.dash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains a list of all cloudflare IP addresses.
 */
public final class ModCloudflare {
    
    public static final List<String> CF_DEFAULT_TRUSTED_PROXY = new ArrayList<>(Arrays.asList(
            /* IPv4 Address Ranges */
            "103.21.244.0",
            "103.22.200.0",
            "103.31.4.0",
            "104.16.0.0",
            "108.162.192.0",
            "131.0.72.0",
            "141.101.64.0",
            "162.158.0.0",
            "172.64.0.0",
            "173.245.48.0",
            "188.114.96.0",
            "190.93.240.0",
            "197.234.240.0",
            "198.41.128.0",
            /* IPv6 Address Ranges */
            "2400:cb00",
            "2405:8100",
            "2405:b500",
            "2606:4700",
            "2803:f800",
            "2c0f:f248",
            "2a06:98c0"
    ));
    
    public static boolean contains(String IP) {
        return CF_DEFAULT_TRUSTED_PROXY.contains(IP);
    }
}
