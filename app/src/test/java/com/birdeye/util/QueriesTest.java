package com.birdeye.util;

import org.junit.Test;

import static com.birdeye.util.Queries.query;
import static com.birdeye.util.Queries.queryStr;
import static com.birdeye.util.Queries.split;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class QueriesTest {
  @Test public void splits() throws Exception {
    assertThat(split("hahaha", "@")).isEqualTo(singletonList("@hahaha"));
    assertThat(split("hahah hahah", "@")).isEqualTo(asList("@hahah", "@hahah"));
    assertThat(split("", "@")).isEqualTo(emptyList());
    assertThat(split("hahah, hahah", "@")).isEqualTo(asList("@hahah", "@hahah"));
    assertThat(split("yep", "#")).isEqualTo(singletonList("#yep"));
    assertThat(split("#photo,#people", "#")).isEqualTo(asList("#photo", "#people"));
  }

  @Test public void queryStrs() {
    assertThat(queryStr("1", "2")).isEqualTo("@1 AND #2");
    assertThat(queryStr("1", "")).isEqualTo("@1");
    assertThat(queryStr("", "")).isEqualTo("");
    assertThat(queryStr("1 2 3", "1 2 3")).isEqualTo("@1 AND @2 AND @3 AND #1 AND #2 AND #3");
    assertThat(queryStr("", "2")).isEqualTo("#2");
  }

  @Test public void queries() {
    assertThat(query("1", "2")).isEqualTo("@1 AND #2 -filter:retweets");
  }
}
