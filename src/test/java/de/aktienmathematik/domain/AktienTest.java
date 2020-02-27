package de.aktienmathematik.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import de.aktienmathematik.web.rest.TestUtil;

public class AktienTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Aktien.class);
        Aktien aktien1 = new Aktien();
        aktien1.setId(1L);
        Aktien aktien2 = new Aktien();
        aktien2.setId(aktien1.getId());
        assertThat(aktien1).isEqualTo(aktien2);
        aktien2.setId(2L);
        assertThat(aktien1).isNotEqualTo(aktien2);
        aktien1.setId(null);
        assertThat(aktien1).isNotEqualTo(aktien2);
    }
}
