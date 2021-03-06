package es.moki.ratelimij.dropwizard.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ResourceInfo;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Deprecated
class IpKeyTest {

    private HttpServletRequest request = mock(HttpServletRequest.class);

    private ResourceInfo resource = mock(ResourceInfo.class);

    @BeforeEach
    void beforeEach() throws Exception {
        doReturn(Object.class).when(resource).getResourceClass();
        when(resource.getResourceMethod()).thenReturn(Object.class.getMethod("wait"));
    }

    @DisplayName("IP key should start with 'rlj' prefix")
    @Test
    void shouldStartWithPrefix() {
        when(request.getRemoteAddr()).thenReturn("293.0.120.7");

        Optional<CharSequence> keyName = Key.IP.create(request, resource, null);

        assertThat(keyName.get()).startsWith("rlj:");
    }

    @DisplayName("IP key should include Class and Method names in key")
    @Test
    void shouldIncludeResourceInKey() {
        when(request.getRemoteAddr()).thenReturn("293.0.120.7");

        Optional<CharSequence> keyName = Key.IP.create(request, resource, null);

        assertThat(keyName.get()).contains("java.lang.Object#wait");
    }

    @DisplayName("IP key should include X-Forwarded-For if available")
    @Test
    void shouldIncludeXForwardedForIfUserNull() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("293.0.113.7,  211.1.16.2");

        Optional<CharSequence> keyName = Key.IP.create(request, resource, null);

        assertThat(keyName.get()).contains("xfwd4#293.0.113.7");
    }

    @DisplayName("IP key should include remote IP if available and user and X-Forwarded-For not found")
    @Test
    void shouldIncludeRemoteIpIfUserAndXForwarded4Null() {
        when(request.getRemoteAddr()).thenReturn("293.0.120.7");

        Optional<CharSequence> keyName = Key.IP.create(request, resource, null);

        assertThat(keyName.get()).contains("ip#293.0.120.7");
    }

    @DisplayName("IP key should return absent if no key available")
    @Test
    void shouldBeAbsent() {
        when(request.getRemoteAddr()).thenReturn(null);

        Optional<CharSequence> keyName = Key.IP.create(request, resource, null);

        assertThat(keyName).isNotPresent();
    }

}