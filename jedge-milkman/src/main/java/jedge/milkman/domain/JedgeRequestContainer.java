package jedge.milkman.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import milkman.domain.RequestContainer;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JedgeRequestContainer extends RequestContainer {

	private String edgeDbDsn;

	
	@Override
	public String getType() {
		return "EQL";
	}

	@Override
	public RequestTypeDescriptor getTypeDescriptor() {
		return new RequestTypeDescriptor("EQL");
	}

	public JedgeRequestContainer(String name, String edgeDbDsn) {
		super(name);
		this.edgeDbDsn = edgeDbDsn;
	}
	
}
