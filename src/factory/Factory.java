package factory;
import client.Main;

public abstract class Factory {
	
	private Main main;
	
	public Factory(Main main) {
		this.setMain(main);
	}

	public void setMain(Main main) {
		this.main = main;
	}

	public Main getMain() {
		return main;
	}
}
