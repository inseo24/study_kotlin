# System.out.println()는 어떻게 동작할까

## 출처

이펙티브 코틀린 책에 작은 주석으로 달린 [링크](https://luckytoilet.wordpress.com/2010/05/21/how-system-out-println-really-works/)가 있었고 그걸 번역했다. 

나중에 다시 읽어보면 도움이 될 거 같다.


### 먼저

System.out 이 무엇이고 어떻게 되어있는지 이해해보자.

System.java를 찾아보면 아래와 같이 정의되어 있다.

```java
public final static PrintStream out = nullPrintStream();
```

여기서 nullPrintStream()의 코드를 찾아보면 아래와 같다.

```java
private static PrintStream nullPrintStream() throws NullPointerException {
    if (currentTimeMillis() > 0) {
        return null;
    }
    throw new NullPointerException();
}
```

nullPrintStream()은 단순히 null을 리턴하거나 예외를 던진다. 

왜 이렇게 될까?

답은 initializeSystemClass()에서 찾을 수 있다.

```java
FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
setOut0(new PrintStream(new BufferedOutputStream(fdOut, 128), true));
```

위의 코드에서 많은 것들이 이뤄진다. 

나중에 언급하겠지만 setOut0()는 실제로 System.out를 초기화한다.

setOut0()는 native function이다. 

System.c에서 구현을 확인할 수 있다.

```c
JNIEXPORT void JNICALL
Java_java_lang_System_setOut0(JNIEnv *env, jclass cla, jobject stream)
{
    jfieldID fid =
        (*env)->GetStaticFieldID(env,cla,"out","Ljava/io/PrintStream;");
    if (fid == 0)
        return;
    (*env)->SetStaticObjectField(env,cla,fid,stream);
}
```

위 코드는 System.out를 인자로 받는 JNI 코드다. 

먼저 System.out를 nullPrintStream()으로 셋팅하고 후에 JNI로 완전히 세팅한다. 왜 이렇게 될까?

자바에서, static field가 먼저 초기화되고, 다른 것은 그 이후에 이뤄진다. 그러므로 JVM 전에 System 클래스가 완전히 초기화되기 전에도 JVM는 System.out를 초기화하려고 시도한다.

이 시점에 JVM의 다른 프로퍼티들은 아직 초기화되어 있지 않기 때문에 System.out를 세팅하는게 불가능하다. 가장 좋은 것은 null로 셋팅해두는 거다. 

System.out과 함께 System 클래스는 static과 스레드 초기화 후 JVM이 호출하는 initializeSystemClass()에서 올바르게 초기화 된다.

하지만 문제가 있는데, System.out이 마지막이기 떄문에 initializeSystemClass()에서 다른 걸로 설정할 수가 없다. 다른 방법으로 네이티브 코드를 사용하면 최종 변수를 수정할 수 있다.

### 파일 디스크립터가 뭔데?

```java
FileOutputStream fdOut = new FileOutputStream(FileDescriptor.out);
```

FileOutputStream 객체는 FileDescriptor.out에서 생성된다. 

FileDescriptor 클래스는 java.io의 일부이지만 OpenJDK의 [java.io](http://java.io/) 디렉토리에서 찾기 어려워 파악하기 힘들다.

이는 FileDescriptor가 대부분의 Java 표준 라이브러리보다 훨씬 low하기 때문이다. 대부분의 .java 파일은 플랫폼 독립적이지만, 실제로 다른 플랫폼에 대한 FileDescriptor의 구현이 다릅니다.

여기서는 Linux/Solaris 버전의 [FileDescriptor.java](http://hg.openjdk.java.net/jdk7/jdk7/jdk/file/cf44386c8fe3/src/solaris/classes/java/io/FileDescriptor.java) 를 사용할 거다.

FileDescriptor는 굉장히 간단하다. 

본질적으로 FileDescriptor는 실제 갖고 있는 게 정수뿐이다. 다른 데이터도 갖고 있긴 하지만, 별로 중요하지 않다. FileDescriptor의 생성자는 정수를 갖고 그 정수를 포함하는 FileDescriptor를 만듭니다.

FileDescriptor는 FileOutputStream object를 초기화할 때 사용한다. 

FileDescriptor.out의 정의를 확인해보자.

```java
public static final FileDescriptor out = new FileDescriptor(1);
```

FileDescriptor.out는 1로 정의되며, in는 0으로, err는 2로 정의된다. 이런 정의의 기초는 유닉스 어딘가 low level에 나온다.

### java.io를 확인해보자

이제 PrintStream의 println()를 확인해보자. 

```java
public void println(String x) {
    synchronized (this) {
        print(x);
        newLine();
    }
}
```

print()의 call stack를 따라가보자.

```java
public void print(String s) {
    if (s == null) {
        s = "null";
    }
    write(s);
}
```

더 딥하게 write()를 따라가보자.

```java
try {
        synchronized (this) {
            ensureOpen();
            textOut.write(s);
            textOut.flushBuffer();
            charOut.flushBuffer();
            if (autoFlush && (s.indexOf('\n') >= 0))
                out.flush();
        }
    }
    catch (InterruptedIOException x) {
        Thread.currentThread().interrupt();
    }
    catch (IOException x) {
        trouble = true;
    }
}
```

내부적으로 PrintStream object는 3가지 다른 object를 포함한다.

- OutputStreamWriter 객체(charOut), 스트림에 문자 배열 쓰기
- 문자 배열뿐만 아니라 문자열과 텍스트도 쓰는 BufferedWriter 객체(textOut)
- BufferedOutputStream 객체(out)는 call stack까지 전달되고 PrintStream 수준에서 훨씬 low하게 사용된다

PrintStream.write()는 BufferedWriter.write()를 호출하고 두 버퍼를 플러시한다. BufferedWriter.java에서 write()의 구현은 추상 클래스 Writer.java에 정의 되어 있다.

```java
public void write(String str) throws IOException {
    write(str, 0, str.length());
}
```

BufferedWriter로 돌아가자

```java
public void write(String s, int off, int len) throws IOException {
    synchronized (lock) {
        ensureOpen();
 
        int b = off, t = off + len;
        while (b < t) {             int d = min(nChars - nextChar, t - b);             s.getChars(b, b + d, cb, nextChar);             b += d;             nextChar += d;             if (nextChar >= nChars)
                flushBuffer();
        }
    }
}
```

이름이 말하는대로, BufferedWriter는 buffered 된다. 데이터는 한 번에 쓰여지거나 플러시 될 때까지 data buffer에 저장된다. Buffered IO는 한 번에 1바이트씩 쓰는 것보다 하드웨어에 쓰는 것보다 빠르다.

BufferedWriter.write() 함수는 실제로 아무것도 쓰지 않는다. 내부 버퍼에만 무언가를 저장한다. 플러싱은 여기서 일어나는 것이 아니라 PrintStream.write()에서 이루어집니다.

flushBuffer()를 확인해보자.

```java
void flushBuffer() throws IOException {
    synchronized (lock) {
        ensureOpen();
        if (nextChar == 0)
            return;
        out.write(cb, 0, nextChar);
        nextChar = 0;
    }
}
```

Writer object(out)에서도 write() 함수를 볼 수 있다. 여기서 out object는 PrintStream의 charOut object이고, OutputStreamWriter 타입을 갖는다. 이 객체는 PrintStream의 charOut와 같은 객체다.

OutputStreamWriter.java에서의 write()를 확인해보자.

```java
public void write(char cbuf[], int off, int len) throws IOException {
    se.write(cbuf, off, len);
}
```

이제 다른 객체로 job를 보내는데 그 객체 타입이 StreamEncoder다. 

StreamEncoder.java를 확인해보자.

```java
public void write(char cbuf[], int off, int len) throws IOException {
    synchronized (lock) {
        ensureOpen();
        if ((off < 0) || (off > cbuf.length) || (len < 0) ||                 ((off + len) > cbuf.length) || ((off + len) > 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        implWrite(cbuf, off, len);
    }
}
```

여기서 StreamEncoder.implWrite()로 이동해보자.

```java
void implWrite(char cbuf[], int off, int len)
    throws IOException
{
    CharBuffer cb = CharBuffer.wrap(cbuf, off, len);
 
    if (haveLeftoverChar)
        flushLeftoverChar(cb, false);
 
    while (cb.hasRemaining()) {
        CoderResult cr = encoder.encode(cb, bb, false);
        if (cr.isUnderflow()) {
            assert (cb.remaining() <= 1) : cb.remaining();             if (cb.remaining() == 1) {                 haveLeftoverChar = true;                 leftoverChar = cb.get();             }             break;         }         if (cr.isOverflow()) {             assert bb.position() > 0;
            writeBytes();
            continue;
        }
        cr.throwException();
    }
}
```

여기서 다시 writeBytes()를 호출한다. 해당 구현은 아래와 같다.

```java
private void writeBytes() throws IOException {
    bb.flip();
    int lim = bb.limit();
    int pos = bb.position();
    assert (pos <= lim);
    int rem = (pos <= lim ? lim - pos : 0);     if (rem > 0) {
        if (ch != null) {
            if (ch.write(bb) != rem)
                assert false : rem;
        } else {
            out.write(bb.array(), bb.arrayOffset() + pos, rem);
        }
    }
    bb.clear();
}
```

이제 StreamEncoder를 끝냈다. 이 클래스는 문자 스트림을 처리하거나 인코딩하지만, 결국은 바이트를 BufferedOutputStream에 다시 작성하는 작업을 넘겨준다.

BufferedOutputStream.java의 write() 코드를 살펴보자.

```java
public synchronized void write(byte b[], int off, int len) throws IOException {
    if (len >= buf.length) {
        /* If the request length exceeds the size of the output buffer,
           flush the output buffer and then write the data directly.
           In this way buffered streams will cascade harmlessly. */
        flushBuffer();
        out.write(b, off, len);
        return;
    }
    if (len > buf.length - count) {
        flushBuffer();
    }
    System.arraycopy(b, off, buf, count, len);
    count += len;
}
```

BufferedOutputStream는 바통을 다시 받고, 이제 FileOutputStream의 차례다. fdOut를 인스턴스화 했을 때 이 아래에서 수많은 시스템 콜이 이뤄진 것이다. 

FileOutputStream은 JNI 전의 마지막 단계다. [FileOutputStream.java](http://FileOutputStream.java) 의 write()를 살펴보자.

```java
public void write(byte b[], int off, int len) throws IOException {
    writeBytes(b, off, len);
}
```

그리고 writeBytes는 아래와 같다.

```java
private native void writeBytes(byte b[], int off, int len) throws IOException;
```

여기가 자바 파트의 끝이다. 아직 끝난 건 아니다.

### [java.io](http://java.io) call stack

아래 차트를 포함한다. 

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/45ba5cec-8417-4e67-a460-09b58cdfd117/Untitled.png)

전체 call stack은 아래와 같다.

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/11579a8c-e9f9-4a1f-bdb0-d4956497327d/Untitled.png)

### JNI로 넘어가서

FileOutputStream 이후에, 콘솔에 바이트를 쓰는 건 native하게 처리된다. 많은 native 코드는 플랫폼에 종속적이다. 윈도우나 리눅스에 따라 코드 버전이 다른다. 여기서는 일단 리눅스 버전을 다룰 것이다.

FileOutputStream_md.c 에 정의된 writeBytes() 구현을 살펴보자.

```java
JNIEXPORT void JNICALL
Java_java_io_FileOutputStream_writeBytes(JNIEnv *env,
    jobject this, jbyteArray bytes, jint off, jint len) {
    writeBytes(env, this, bytes, off, len, fos_fd);
}
```

Fos_fd 필드는 위에서 봤던 FileDescriptor 객체에 저장된 정수다. 아웃 스트림의 경우, fos_fd는 1이어야 한다.

fos_id의 추가 인수와 함께 메서드 writeBytes를 호출하고 있다. writeBytes()의 구현은 io_util.c에 정의되어 있다.

```c
void writeBytes(JNIEnv *env, jobject this, jbyteArray bytes,
           jint off, jint len, jfieldID fid)
{
    jint n;
    char stackBuf[BUF_SIZE];
    char *buf = NULL;
    FD fd;
 
    if (IS_NULL(bytes)) {
        JNU_ThrowNullPointerException(env, NULL);
        return;
    }
 
    if (outOfBounds(env, off, len, bytes)) {
        JNU_ThrowByName(env, "java/lang/IndexOutOfBoundsException", NULL);
        return;
    }
 
    if (len == 0) {
        return;
    } else if (len > BUF_SIZE) {
        buf = malloc(len);
        if (buf == NULL) {
            JNU_ThrowOutOfMemoryError(env, NULL);
            return;
        }
    } else {
        buf = stackBuf;
    }
 
    (*env)->GetByteArrayRegion(env, bytes, off, len, (jbyte *)buf);
 
    if (!(*env)->ExceptionOccurred(env)) {
        off = 0;
        while (len > 0) {
            fd = GET_FD(this, fid);
            if (fd == -1) {
                JNU_ThrowIOException(env, "Stream Closed");
                break;
            }
            n = IO_Write(fd, buf+off, len);
            if (n == JVM_IO_ERR) {
                JNU_ThrowIOExceptionWithLastError(env, "Write error");
                break;
            } else if (n == JVM_IO_INTR) {
                JNU_ThrowByName(env, "java/io/InterruptedIOException", NULL);
                break;
            }
            off += n;
            len -= n;
        }
    }
    if (buf != stackBuf) {
        free(buf);
    }
}
```

쓰는 건 IO_Write 메소드에 의해 수행된다. 이 시점에서, IO_Write는 윈도우와 리눅스에서 각각 다르게 정의되기 때문에 다음에 일어나는 일은 플랫폼에 따라 달라진다.

### 리눅스에서는

리눅스에서 IO를 다루는 건 HPI(Hardware Platform Interface)를 사용한다. 그러므로, io_util_md.h에 JVM_Write가 정의된다.

```c
#define IO_Write JVM_Write
```

JVM_Write 코드는 JVM 안에 정의되어 있다. 이 코드는 C++로 되어 있고 jvm.cpp 에서 확인할 수 있다.

```cpp
JVM_LEAF(jint, JVM_Write(jint fd, char *buf, jint nbytes))
  JVMWrapper2("JVM_Write (0x%x)", fd);
 
  //%note jvm_r6
  return (jint)os::write(fd, buf, nbytes);
JVM_END
```

여기서부터는 HPI 메서드로 이뤄져있다. 더 찾아보려면 할 수 있겠지만 이건 일단 여기서는 여기까지만 보자. 

### 윈도우에서는

윈도우에서, IO_Write 메서드는 HPI가 아닌 io_util_md.h에 handleWirte로 재정의된다.

```c
JNIEXPORT
size_t
handleWrite(jlong fd, const void *buf, jint len)
{
    BOOL result = 0;
    DWORD written = 0;
    HANDLE h = (HANDLE)fd;
    if (h != INVALID_HANDLE_VALUE) {
        result = WriteFile(h,           /* File handle to write */
                      buf,              /* pointers to the buffers */
                      len,              /* number of bytes to write */
                      &written,         /* receives number of bytes written */
                      NULL);            /* no overlapped struct */
    }
    if ((h == INVALID_HANDLE_VALUE) || (result == 0)) {
        return -1;
    }
    return written;
}
```

WriteFile 함수는 Windos API에 있다. 윈도우 API는 오픈 소스가 아니니, 여기서 멈추도록 하자.

### 결론

System.out의 인스턴스화부터 java.io가 어떻게 거치고, JNI 및 HPI 레벨 IO 처리까지 System.out.println()의 전체 call stack을 확인해봤다.

HPI 계층과 WriteFile API 호출 아래에 수십 개의 레벨이 더 있기 때문에 끝까지 본 것도 아니다. 

아마도 "System.out.println()은 어떻게 작동하나요?"라는 질문에 대한 더 나은 대답은 "마법이다"일 것이다.
