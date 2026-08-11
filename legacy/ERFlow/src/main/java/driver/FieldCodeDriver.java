package driver;

import repository.FieldCodeRepository;

public class FieldCodeDriver {
	public static void main(String[] args) {
		FieldCodeRepository repository = FieldCodeRepository.getInstance();
		
		//System.out.println(repository.getFieldCodeKeys());
		repository.getFieldCodes("운송").forEach(System.out::println);
	}
}
