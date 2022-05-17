package jedge.milkman.domain;

import lombok.Data;
import milkman.domain.RequestAspect;

@Data
public class EqlRequestAspect extends RequestAspect {

  String eql;

  public EqlRequestAspect() {
    super("eql");
  }
}
