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
