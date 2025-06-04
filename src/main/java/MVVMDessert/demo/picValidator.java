package MVVMDessert.demo;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class picValidator implements ConstraintValidator<picRule, String> {

	private picRule picRule;

	@Override
	public void initialize(picRule constraintAnnotation) {
		ConstraintValidator.super.initialize(constraintAnnotation);
		this.picRule = constraintAnnotation;
	}

	@Override
	public boolean isValid(String pic, ConstraintValidatorContext context) {

		if (pic == null) {
			return true;// validate 在Not null做掉了
		}

		String imageString = pic.substring(5, 10);

		if (!(imageString.equals("image"))) {
			return false;
		}
//
//		System.out.println("11" + pic.contains("data"));
//		if (!(pic.contains("image"))) {
//			return true;
//		}

		return true;
	}

}
