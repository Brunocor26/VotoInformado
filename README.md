# 🗳️ VotoInformado

<div align="center">

![VotoInformado Logo](app/src/main/res/drawable/logo.png)

**Mantenha-se informado sobre as eleições em Portugal**

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![API Level](https://img.shields.io/badge/API-33%2B-brightgreen.svg)](https://android-arsenal.com/api?level=33)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

</div>

---

## 📱 Sobre o Projeto

**VotoInformado** é uma aplicação Android desenvolvida para ajudar os cidadãos portugueses a manterem-se informados sobre o processo eleitoral. A aplicação fornece informações detalhadas sobre candidatos, sondagens, notícias políticas e datas importantes relacionadas com eleições.

### ✨ Funcionalidades Principais

- 🎯 **Informação de Candidatos**: Visualize perfis detalhados dos candidatos, incluindo biografia, partido político, profissão e propostas
- 📊 **Sondagens Eleitorais**: Acompanhe as últimas sondagens com gráficos interativos e análises detalhadas
- 📰 **Notícias**: Mantenha-se atualizado com as últimas notícias políticas
- 📅 **Datas Importantes**: Nunca perca eventos importantes do calendário eleitoral
- 🔐 **Autenticação Segura**: Login/Registo com Firebase Authentication e suporte para Google Sign-In
- 🌓 **Modo Escuro**: Alterne entre modo claro e escuro para melhor experiência visual
- 📈 **Visualizações Gráficas**: Gráficos detalhados e interativos para análise de dados eleitorais

---

## 🛠️ Tecnologias Utilizadas

### Linguagem e Framework
- **Java** - Linguagem de programação principal
- **Android SDK** - API Level 33+ (Android 13)

### Bibliotecas e Dependências

#### Firebase
- Firebase Authentication - Autenticação de utilizadores
- Firebase Analytics - Análise de comportamento dos utilizadores
- Firebase Storage - Armazenamento de ficheiros

#### Rede e Dados
- **Retrofit 2.9.0** - Cliente HTTP para comunicação com APIs
- **Gson 2.10.1** - Serialização/deserialização JSON

#### Interface de Utilizador
- **Material Components** - Design seguindo Material Design guidelines
- **MPAndroidChart v3.1.0** - Biblioteca para criação de gráficos
- **Picasso** - Carregamento e cache de imagens
- **CircleImageView 3.1.0** - Imagens de perfil circulares

#### Autenticação
- **Credential Manager** - Gestão de credenciais do Google
- **Google ID** - Integração com Google Sign-In

#### Testes
- JUnit - Testes unitários
- Espresso - Testes de UI
- AndroidX Test - Framework de testes

---

## 📂 Estrutura do Projeto

```
VotoInformado/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/pt/ubi/pdm/votoinformado/
│   │   │   │   ├── activities/       # Activities principais
│   │   │   │   │   ├── HomeActivity.java
│   │   │   │   │   ├── LoginActivity.java
│   │   │   │   │   ├── CandidatoDetailActivity.java
│   │   │   │   │   ├── SondagemDetailActivity.java
│   │   │   │   │   └── SettingsActivity.java
│   │   │   │   ├── fragments/        # Fragments da UI
│   │   │   │   │   ├── HomeFragment.java
│   │   │   │   │   ├── CandidatosFragment.java
│   │   │   │   │   ├── SondagensFragment.java
│   │   │   │   │   └── NoticiasFragment.java
│   │   │   │   ├── adapters/         # RecyclerView Adapters
│   │   │   │   ├── classes/          # Modelos de dados
│   │   │   │   │   ├── Candidato.java
│   │   │   │   │   ├── Sondagem.java
│   │   │   │   │   └── ImportantDate.java
│   │   │   │   ├── parsing/          # Utilitários JSON
│   │   │   │   ├── utils/            # Classes utilitárias
│   │   │   │   └── prefs/            # Gestão de preferências
│   │   │   ├── res/
│   │   │   │   ├── layout/           # Layouts XML
│   │   │   │   ├── drawable/         # Recursos gráficos
│   │   │   │   ├── values/           # Strings, cores, temas
│   │   │   │   └── menu/             # Menus
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                     # Testes unitários
│   │   └── androidTest/              # Testes instrumentados
│   ├── build.gradle.kts
│   └── google-services.json          # Configuração Firebase
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

## 🚀 Instalação e Configuração

### Pré-requisitos

- **Android Studio** (última versão recomendada)
- **JDK 17** ou superior
- **Android SDK** com API Level 33 ou superior
- Conta **Firebase** (para funcionalidades de autenticação e storage)

### Passos de Instalação

1. **Clone o repositório**
   ```bash
   git clone https://github.com/Brunocor26/VotoInformado.git
   cd VotoInformado
   ```

2. **Configurar Firebase**
   - Crie um projeto no [Firebase Console](https://console.firebase.google.com/)
   - Adicione uma aplicação Android com o package name: `pt.ubi.pdm.votoinformado`
   - Faça download do ficheiro `google-services.json`
   - Coloque o ficheiro na pasta `app/`

3. **Configurar Google Sign-In**
   - No Firebase Console, ative a autenticação por Google
   - Configure as credenciais OAuth 2.0 no Google Cloud Console

4. **Abrir no Android Studio**
   - Abra o Android Studio
   - Selecione "Open an existing project"
   - Navegue até à pasta do projeto clonado
   - Aguarde a sincronização do Gradle

5. **Build e Run**
   ```bash
   ./gradlew build
   ```
   - Conecte um dispositivo Android ou inicie um emulador
   - Clique em "Run" no Android Studio

---

## 📖 Como Usar

### Primeira Utilização

1. **Registo/Login**
   - Ao abrir a app, crie uma conta com email e password
   - Ou faça login com a sua conta Google

2. **Explorar Candidatos**
   - Navegue pelo separador "Candidatos"
   - Toque num candidato para ver informações detalhadas
   - Consulte biografia, propostas e informações de contacto

3. **Consultar Sondagens**
   - Aceda ao separador "Sondagens"
   - Visualize gráficos interativos com os resultados
   - Consulte detalhes metodológicos de cada sondagem

4. **Acompanhar Notícias**
   - Mantenha-se atualizado através do separador "Notícias"
   - Leia as últimas novidades políticas

5. **Calendário Eleitoral**
   - Consulte datas importantes através do menu
   - Nunca perca debates, eleições ou eventos relevantes

### Personalização

- **Modo Escuro**: Aceda às definições para ativar/desativar o modo escuro
- **Preferências**: Configure as suas preferências na área de Settings

---

## 🎨 Screenshots

<!-- Adicione screenshots da aplicação aqui -->
*Screenshots serão adicionados em breve*

---

## 🧪 Testes

Para executar os testes unitários:
```bash
./gradlew test
```

Para executar os testes instrumentados:
```bash
./gradlew connectedAndroidTest
```

---

## 🤝 Contribuir

Contribuições são bem-vindas! Para contribuir:

1. Faça fork do projeto
2. Crie uma branch para a sua feature (`git checkout -b feature/MinhaFeature`)
3. Commit as suas alterações (`git commit -m 'Adiciona MinhaFeature'`)
4. Push para a branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

### Diretrizes de Contribuição

- Siga as convenções de código Java existentes
- Adicione testes para novas funcionalidades
- Atualize a documentação quando necessário
- Mantenha commits claros e descritivos

---

## 📝 Roadmap

- [ ] Implementar notificações push para eventos importantes
- [ ] Adicionar suporte para múltiplas línguas
- [ ] Integrar mais fontes de notícias
- [ ] Adicionar comparação direta entre candidatos
- [ ] Implementar sistema de favoritos
- [ ] Criar widget para ecrã inicial

---

## 👥 Autores

- **Bruno** - [Brunocor26](https://github.com/Brunocor26)

---

## 📄 Licença

Este projeto está licenciado sob a licença MIT - veja o ficheiro [LICENSE](LICENSE) para mais detalhes.

---

## 📞 Contacto

Para questões ou sugestões:
- GitHub Issues: [https://github.com/Brunocor26/VotoInformado/issues](https://github.com/Brunocor26/VotoInformado/issues)

---

## 🙏 Agradecimentos

- Universidade da Beira Interior (UBI) - Programação de Dispositivos Móveis (PDM)
- Firebase pela infraestrutura backend
- Comunidade Android pela documentação e suporte
- PhilJay pelo MPAndroidChart

---

<div align="center">

**Feito com ❤️ para promover uma democracia mais informada**

⭐ Se este projeto foi útil, considera dar uma estrela!

</div>