# Copilot Instructions
 
Este archivo define los lineamientos que Copilot debe seguir al generar código, documentación o sugerencias para este proyecto Java, en concordancia con las prácticas, estándares y restricciones tecnológicas de la organización.
 
---
 
## 1. Lenguajes y Tecnologías Permitidas ✅
 
- **Lenguaje principal:** Java (LTS)  
  - Microservicios: Java 17 (LTS)  
  - WebLogic 12.2.1.4 con Java 1.8 (LTS) solo para casos heredados aprobados.
- **Frameworks backend:** Spring Boot (incluyendo WebFlux en contexto Lego oficial).
- **Frameworks frontend (si aplica):** Angular (versión oficial Lego), PrimeNG, Bootstrap 5.3 (sin vulnerabilidades conocidas).
- **Bases de datos:** Oracle 19C, PostgreSQL Flexible, Cosmos DB, SQL Server (solo en casos permitidos).
- **Plataformas y servicios:** AKS ≥ 1.27, App Services Azure, Azure Functions, Logic Apps, Service Bus (con restricciones conocidas).
 
---
 
## 2. Patrones de Diseño y Arquitectura
 
- **Patrones obligatorios:** Service, DTO, Controller.
- **Estructura de carpetas:**
└── main/
    ├── java/
    │   └── com.tuempresa.tuproyecto/
    │       ├── controller/       # Controladores REST
    │       ├── service/          # Lógica de negocio
    │       ├── dto/              # Objetos de transferencia
    │       ├── model/            # Entidades del dominio
    │       ├── repository/       # Acceso a datos
    │       └── config/           # Configuraciones generales
    └── resources/
        └── application.properties
 
- **Buenas prácticas:**
- Nombres claros y consistentes (`UserService`, `UserDto`, etc.).
- Lógica de negocio en `service/`, no en controladores.
- Validaciones en DTOs o con anotaciones (`@Valid`).
- Documentar métodos públicos con Javadoc.
 
---
 
## Estructura recomendada de carpetas para frontend (Angular)
 
Se debe implementar la siguiente estructura de carpetas para proyectos frontend en Angular, asegurando claridad y mantenibilidad sin sobreingeniería:
 
```
frontend/
├── src/
│   ├── app/
│   │   ├── core/           # Servicios globales, guards, interceptors
│   │   ├── shared/         # Componentes, pipes y directivas reutilizables
│   │   ├── features/       # Módulos funcionales (por dominio o caso de uso)
│   │   ├── assets/         # Imágenes, fuentes, etc.
│   │   └── environments/   # Configuración de entornos
│   ├── index.html
│   └── styles.scss
├── angular.json
├── package.json
└── README.md
```
 
- `core/`: Servicios y lógica global (autenticación, manejo de errores, etc.).
- `shared/`: Componentes, pipes y directivas reutilizables en todo el proyecto.
- `features/`: Módulos por funcionalidad o dominio (ejemplo: usuario, adopción, dashboard).
- `assets/`: Recursos estáticos.
- `environments/`: Configuración para distintos entornos (dev, prod).
 
Esta estructura debe ser utilizada salvo que existan restricciones técnicas justificadas.
 
---
 
## 3. Calidad de Código (Quality Gates)
 
Copilot debe generar código que cumpla con los siguientes umbrales:
 
| Métrica | Umbral |
| --- | --- |
| Cobertura de pruebas | ≥ 80% |
| Duplicación de código | ≤ 3% |
| Mantenibilidad | Ratio A (deuda técnica < 5%) |
| Fiabilidad | Ratio A (sin bugs) |
| Seguridad | Ratio A (sin vulnerabilidades) |
| Puntos de acceso de seguridad revisados | 100% |
 
---
 
## 4. CI/CD y Automatización
 
- Generar pipelines que incluyan:
- Compilación y empaquetado.
- Ejecución de pruebas unitarias y de integración.
- Evaluación de Quality Gates en SonarQube.
- Versionado y promoción de artefactos.
- Despliegue automatizado en entornos aprobados.
 
---
 
## 5. Observabilidad y Monitoreo
 
- Incluir hooks o configuración para:
- Monitoreo de ambientes productivos y pre-productivos.
- Métricas de rendimiento y logs estructurados.
- Integración con herramientas de observabilidad aprobadas.
 
---
 
## 6. Seguridad
 
- Usar librerías con licencia válida y soporte vigente.
- Evitar dependencias con vulnerabilidades conocidas (verificar en CVE y Snyk).
- Implementar validaciones de entrada y sanitización de datos.
- Cumplir con las reglas de inyección y configuración segura.
 
---
 
## 7. Restricciones y Prohibiciones
 
- No generar código en tecnologías obsoletas o no aprobadas (lista roja).
- No usar versiones beta, release candidate o privadas en producción.
- No incluir lógica de negocio en bases de datos (PL/SQL, triggers complejos) salvo excepciones aprobadas.
- No generar código que incumpla la política de versiones mínimas.
 
---
 
## 8. Estilo y Legibilidad
 
- Seguir la convención de nombres Java (CamelCase para clases, lowerCamelCase para métodos y variables).
- Usar sangría de 4 espacios.
- Limitar métodos a una responsabilidad clara.
- Incluir comentarios solo cuando aporten valor y no sean redundantes.
 
---
 
## 9. Documentación y Comunicación
 
- Generar README y documentación técnica en Markdown.
- Incluir diagramas de arquitectura cuando se agreguen nuevos módulos.
 
---
 
**Nota:** Cualquier sugerencia generada por Copilot que no cumpla con estos lineamientos debe ser revisada y ajustada antes de ser integrada al código base.
 