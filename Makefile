.PHONY: dev functions-install functions-build rules-deploy-staging rules-deploy-production functions-deploy-staging functions-deploy-production release-checklist

dev:
	firebase emulators:start

functions-install:
	cd functions && npm ci

functions-build:
	cd functions && npm run build

rules-deploy-staging:
	firebase use staging && firebase deploy --only firestore:rules,storage

rules-deploy-production:
	firebase use production && firebase deploy --only firestore:rules,storage

functions-deploy-staging:
	firebase use staging && firebase deploy --only functions

functions-deploy-production:
	firebase use production && firebase deploy --only functions

release-checklist:
	@echo "Bkz: docs/firebase-native-release-guide.md"
