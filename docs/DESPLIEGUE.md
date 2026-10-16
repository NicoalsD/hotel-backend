# Despliegue en AWS Elastic Beanstalk

El backend se despliega en Elastic Beanstalk, en la región `us-east-1`, como un entorno de una sola instancia. La infraestructura se describe en [infra/cloudformation.json](../infra/cloudformation.json).

## Recursos de la plantilla

| Recurso | Tipo | Descripción |
|---|---|---|
| `WebTierRole` | `AWS::IAM::Role` | Rol de la instancia EC2 con la política administrada `AWSElasticBeanstalkWebTier`. |
| `WebTierInstanceProfile` | `AWS::IAM::InstanceProfile` | Perfil de instancia que usa el rol anterior. |
| `HotelApplication` | `AWS::ElasticBeanstalk::Application` | Aplicación `hotel-backend`. |
| `HotelApplicationVersion` | `AWS::ElasticBeanstalk::ApplicationVersion` | Versión del código tomada de S3. |
| `HotelEnvironment` | `AWS::ElasticBeanstalk::Environment` | Entorno `hotel-backend-prod`, `SingleInstance`, `t3.small`. |

Plataforma: `64bit Amazon Linux 2023 v4.13.0 running Corretto 17`.

## Parámetros

| Parámetro | Valor por defecto | Uso |
|---|---|---|
| `ArtifactKey` | `hotel-backend.jar` | Clave del artefacto en el bucket `hotel-backend-artifacts-532475280984`. |
| `CorsAllowedOrigins` | `http://localhost:5173,https://*.vercel.app` | Orígenes permitidos por CORS, separados por coma. |

## Variables de entorno

| Variable | Valor | Propósito |
|---|---|---|
| `PORT` | `5000` | Puerto en el que escucha la aplicación dentro de la instancia. |
| `CORS_ALLOWED_ORIGINS` | parámetro `CorsAllowedOrigins` | Lo lee `application.properties` como `hotel.cors.allowed-origins`. |

Elastic Beanstalk expone la aplicación en el puerto 80 y la redirige al puerto de la aplicación.

## Pasos de despliegue

1. Compilar y probar el proyecto:

   ```bash
   ./mvnw -B verify
   ```

2. Subir el JAR generado (`target/hotel-backend-0.0.1-SNAPSHOT.jar`) al bucket de artefactos con la clave que usará el parámetro `ArtifactKey`:

   ```bash
   aws s3 cp target/hotel-backend-0.0.1-SNAPSHOT.jar s3://hotel-backend-artifacts-532475280984/hotel-backend.jar
   ```

3. Crear o actualizar la pila de CloudFormation:

   ```bash
   aws cloudformation deploy \
     --template-file infra/cloudformation.json \
     --stack-name hotel-backend-prod \
     --region us-east-1 \
     --capabilities CAPABILITY_NAMED_IAM \
     --parameter-overrides ArtifactKey=hotel-backend.jar
   ```

   `CAPABILITY_NAMED_IAM` es necesario porque la plantilla crea un rol y un perfil de instancia con nombre propio.

4. Verificar el servicio:

   ```bash
   curl http://hotel-backend-grupo.us-east-1.elasticbeanstalk.com/api/hotel/servicios/catalogo
   ```

Para desplegar una versión nueva basta con subir un JAR con el mismo nombre de clave y repetir el paso 3, o usar una nueva clave (por ejemplo, con el número de versión) y pasarla en `ArtifactKey`.

## Notas

- El entorno usa una sola instancia (`SingleInstance`), sin balanceador de carga. Es adecuado para un entorno académico o de demostración, pero no ofrece alta disponibilidad.
- El almacenamiento es en memoria: al reiniciar la instancia se pierden las reservas. Las habitaciones se vuelven a cargar al iniciar.
- La URL pública está fijada con `CNAMEPrefix` a `hotel-backend-grupo`. El nombre del entorno es `hotel-backend-prod`.
- La documentación Swagger en `/swagger-ui.html` estará disponible cuando se agregue springdoc al proyecto.
