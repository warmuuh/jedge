package jedge.milkman.domain;

import lombok.Data;
import milkman.domain.ResponseAspect;

@Data
public class JedgeResponseAspect implements ResponseAspect {

  String result;

  @Override
  public String getName() {
    return "Result";
  }
}
