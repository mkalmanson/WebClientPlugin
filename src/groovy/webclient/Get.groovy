package webclient

import java.lang.annotation.*

/**
 * Created by myles on 1/17/15.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@interface Get {
    Class type()
    String url()
}