Database Management
-------------------

1. Exceptions
   1. Checked
      1. Exception that is extending java.lang.Exception (expect
         java.lang.RuntimeException) class that has to be explicitly declared in
         throws part of method signature of method that is throwing an exception and has
         to be explicitly handled by code that invokes the method. If code that is calling the
         method with checked exception does not handle exception, it has to declare it in
         throws part of method signature.
      2. Pros:
         1. Developer using API always has a list of
            exceptional situations that has to be
            handled
         2. Fast compile-time feedback on check if
            all exceptional situations were handled
      3. Cons:
         1. May result in cluttered code
         2. Coupling between callee and caller
   2. Unchecked
      1. Exception that is extending
         java.lang.RuntimeException class, does not have to be explicitly declared in
         throws part of method signature of method that is throwing an exception and does
         not have to be explicitly handled by code that invokes the method. Developer has
         freedom of choice if error handling should be implemented or not.
      2. Pros:
         1. Reduces cluttered code
         2. Reduces coupling between callee and
            caller
      3. Cons:
         1. May result in missing situations in which
            error handling should be implemented
         2. Lack of compile-time feedback on error
            handling
   3. Spring encourages Runtime exception due to its pros
   4. Spring  data access exception hierarchy
      1. Data Access Exception is a Runtime Exception
      2. Examples of concrete Data Access Exceptions
         1. CannotAcquireLockException
         2. CannotCreateRecordException
         3. DataIntegrityViolationException
      3. Purpose of this hierarchy is to create
         abstraction layer on top of Data Access APIs to
         avoid coupling with concrete implementation
         of Data Access APIs


