import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @FileName StackErrorTest
 * @Description TODO
 * @Author markt
 * @Date 2019-04-08
 * @Version 1.0
 */
@Slf4j
public class StringTest {

	private int num = 4000000;

	@Test
	public void test() {

		long t1 = System.currentTimeMillis();
		String string = "";
		for (int i = 0; i < num; i++) {
			string += "hello";
		}
		long t2 = System.currentTimeMillis();
		log.debug("{}", t2 - t1);

	}

	@Test
	public void testBuilder() {
		long t3 = System.currentTimeMillis();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < num; i++) {
			builder.append("hello");
		}
		builder.toString();
		long t4 = System.currentTimeMillis();
		log.debug("{}", t4 - t3);
	}

	@Test
	public void testBuffer() {

		long t5 = System.currentTimeMillis();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < num; i++) {
			buffer.append("hello");
		}
		buffer.toString();
		long t6 = System.currentTimeMillis();
		log.debug("{}", t6 - t5);
	}
}
