package org.bigdatainc.model.value;

public record Language(String value) {

  public static Language of(final String value) {
    return new Language(value);
  }

  public static Language notFound() {
    return new Language("Not Found");
  }
}
