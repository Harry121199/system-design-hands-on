import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class DnsResolver {
    public static void main(String[] args){
        String[] domains = {
                "google.com",
                "amazom.com",
                "github.com",
                "localhost",
                "this.domain.does.not.exist.xyz"
        };

        // First pass - cold lookup
        System.out.println("=== FIRST PASS ===\n");
        for(String domain : domains){
            resolve(domain);
        }
        // Second pass - should hit cache
        System.out.println("=== SECOND PASS ===\n");
        for(String domain: domains){
            resolve(domain);
        }
    }

    private static void resolve(String domain) {
        System.out.println("\nResolving: "+ domain);
        System.out.println("-".repeat(40));

        try {
            long start = System.nanoTime();
            InetAddress[] addresses = InetAddress.getAllByName(domain);
            long elapsed = (System.nanoTime() - start) / 1_000_000;

            for (InetAddress address : addresses) {
                System.out.println(" IP: " + address.getHostAddress());
                System.out.println(" Type: " + (address instanceof Inet6Address ? "IPv6" : "IPv4"));
                System.out.println(" Canonical: " + address.getCanonicalHostName());
            }

            System.out.println(" Resolved in: " + elapsed + " ms");
            System.out.println(" Addresses found: " + addresses.length);
        } catch (UnknownHostException e) {
            System.out.println(" FAILED:" + e.getMessage());
        }
    }
}